package com.phouthasak.controlHQ.service.tapo;

import com.phouthasak.controlHQ.domain.tapo.TapoAccount;
import com.phouthasak.controlHQ.domain.tapo.TapoDeviceInfo;
import com.phouthasak.controlHQ.model.dto.Device;
import com.phouthasak.controlHQ.service.EnvironmentService;
import com.phouthasak.controlHQ.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class TapoService {
    @Autowired
    private EnvironmentService environmentService;

    private Map<String, TapoAccount> deviceMap;
//    private CanvasFrame canvas;
//    private volatile boolean isCapturing = false;
    private static final int RTSP_PORT = 554;
    private static final String DEFAULT_RTSP_URL = "rtsp://%s:%s@%s:%d/stream1";

    @PostConstruct
    private void init() {
        deviceMap = new HashMap<>();
        List<TapoAccount> accounts = environmentService.getTapoAccounts();

        for (TapoAccount account : accounts) {

            Device device = null;
            try {
                TapoDeviceInfo tapoDeviceInfo = getDeviceInfo(account);
                device = tapoDeviceInfo.toDevice();
            } catch (Exception ex) {
                log.error("Error setting up device info map: " + account.getIp(), ex);
            }

            if (Objects.nonNull(device)) {
                deviceMap.put(device.getId(), account);
            }
        }
    }

    public List<Device> listDevices() {
        List<Device> devices = new ArrayList<>();

        try {
            List<String> deviceIds = new ArrayList<>(deviceMap.keySet());
            for (String deviceId : deviceIds) {
                TapoDeviceInfo tapoDeviceInfo = getDeviceInfo(deviceMap.get(deviceId));
                devices.add(tapoDeviceInfo.toDevice());
            }
        } catch (Exception ex) {
            log.error("Error getting list of devices: ", ex);
        }

        return devices;
    }

    private TapoDeviceInfo getDeviceInfo(TapoAccount tapoAccount) {
        String rtspUrl = String.format(DEFAULT_RTSP_URL,
                tapoAccount.getAccountName(),
                tapoAccount.getAccountPwd(),
                tapoAccount.getIp(),
                RTSP_PORT
        );
        TapoDeviceInfo deviceInfo = null;

        log.info("Connecting to camera: " + tapoAccount.getIp());
        log.info("RTSP URL: " + rtspUrl.replaceAll(tapoAccount.getAccountPwd(), Constants.PRIVATE_CHAR_MASK));

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.start();

            log.info("Camera connected successfully!: " + tapoAccount.getIp());
            log.info("Stream info - Width: " + grabber.getImageWidth() +
                    ", Height: " + grabber.getImageHeight() +
                    ", FPS: " + grabber.getFrameRate());
            deviceInfo = parseDeviceInfo(grabber);
        } catch (FrameGrabber.Exception ex) {
            log.error("Failed to get device info from camera at IP: {}", tapoAccount.getIp(), ex);
        }

        return deviceInfo;
    }

    private TapoDeviceInfo parseDeviceInfo(FFmpegFrameGrabber grabber) {
        if (grabber == null) return null;

        Map<String, String> metaData = grabber.getMetadata();
        metaData.forEach((key, value) -> log.info("{}: {}", key, value));

        TapoDeviceInfo info = TapoDeviceInfo.builder()
                .deviceName(metaData.get("title"))
                .imageWidth(grabber.getImageWidth())
                .imageHeight(grabber.getImageHeight())
                .frameRate(grabber.getFrameRate())
                .videoCodec(grabber.getVideoCodecName())
                .audioCodec(grabber.getAudioCodecName())
                .audioChannels(grabber.getAudioChannels())
                .allMetaData(metaData)
                .build();
        return info;
    }
//
//    // Camera connection parameters
//    private String cameraIp;
//    private String username;
//    private String password;
//
//
//    public TapoService(String cameraIp, String username, String password) {
//        this.cameraIp = cameraIp;
//        this.username = username;
//        this.password = password;
//    }
//
//    /**
//     * Initialize the camera connection and display window
//     */
//    public void initialize() {
//        try {
//            // Construct RTSP URL for Tapo camera
//            String rtspUrl = String.format("rtsp://%s:%s@%s:%d/stream1",
//                    username, password, cameraIp, RTSP_PORT);
//
//            System.out.println("Connecting to camera: " + cameraIp);
//            System.out.println("RTSP URL: " + rtspUrl.replaceAll(password, "****"));
//
//            // Initialize frame grabber
//            grabber = new FFmpegFrameGrabber(rtspUrl);
//            grabber.setOption("rtsp_transport", "tcp"); // Use TCP for more reliable connection
////            grabber.setOption("stimeout", "5000000"); // 5 second timeout
//
//            // Start the grabber
//            grabber.start();
//
//            System.out.println("Camera connected successfully!");
//            System.out.println("Stream info - Width: " + grabber.getImageWidth() +
//                    ", Height: " + grabber.getImageHeight() +
//                    ", FPS: " + grabber.getFrameRate());
//            printAllDeviceInfo(grabber);
//
//            // Create display window
//            canvas = new CanvasFrame("Tapo C100 Camera Feed", CanvasFrame.getDefaultGamma()/grabber.getGamma());
//            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            canvas.setAlwaysOnTop(false);
//
//            // Add window close listener
//            canvas.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    stopCapture();
//                    System.exit(0);
//                }
//            });
//
////            canvas.setVisible(true);
//        } catch (Exception e) {
//            System.err.println("Error initializing camera connection: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void printAllDeviceInfo(FFmpegFrameGrabber grabber) {
//        log.info("----------------------------------------------------");
//        log.info("           Camera Stream Information");
//        log.info("----------------------------------------------------");
//
//        // General Info
//        log.info("Format: {}", grabber.getFormat());
//        log.info("Duration (us): {}", grabber.getLengthInTime());
//
//        // Video Info
//        log.info("--- Video ---");
//        log.info("Image Width: {}", grabber.getImageWidth());
//        log.info("Image Height: {}", grabber.getImageHeight());
//        log.info("Frame Rate: {}", grabber.getFrameRate());
//        log.info("Pixel Format: {}", grabber.getPixelFormat());
//        log.info("Video Codec: {}", grabber.getVideoCodecName());
//        log.info("Video Bitrate: {}", grabber.getVideoBitrate());
//
//        // Audio Info
//        log.info("--- Audio ---");
//        log.info("Audio Channels: {}", grabber.getAudioChannels());
//        log.info("Sample Rate: {}", grabber.getSampleRate());
//        log.info("Audio Codec: {}", grabber.getAudioCodecName());
//        log.info("Audio Bitrate: {}", grabber.getAudioBitrate());
//
//        // Metadata Map
//        if (grabber.getMetadata() != null && !grabber.getMetadata().isEmpty()) {
//            log.info("--- Metadata ---");
//            grabber.getMetadata().forEach((key, value) ->
//                    log.info("{}: {}", key, value)
//            );
//        }
//
//        log.info("----------------------------------------------------");
//    }
//
//    /**
//     * Start capturing and displaying the video feed
//     */
//    public void startCapture() {
//        if (grabber == null) {
//            System.err.println("Camera not initialized. Call initialize() first.");
//            return;
//        }
//
//        isCapturing = true;
//
//        Thread captureThread = new Thread(() -> {
////            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
//
//            try {
//                while (isCapturing && !Thread.currentThread().isInterrupted()) {
//                    Frame frame = grabber.grab();
//
//                    if (frame != null && frame.image != null) {
//                        // Display the frame
//                        canvas.showImage(frame);
//
//                        // Optional: Process the frame (e.g., save screenshots, apply filters)
//                        // processFrame(frame);
//                    }
//
//                    // Small delay to prevent overwhelming the system
//                    Thread.sleep(33); // ~30 FPS
//                }
//            } catch (Exception e) {
//                System.err.println("Error during video capture: " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
//
//        captureThread.setDaemon(true);
//        captureThread.start();
//
//        System.out.println("Video capture started. Press Ctrl+C or close window to stop.");
//    }
//
//    /**
//     * Stop the video capture and clean up resources
//     */
//    public void stopCapture() {
//        System.out.println("Stopping video capture...");
//        isCapturing = false;
//
//        try {
//            if (grabber != null) {
//                grabber.stop();
//                grabber.release();
//            }
//            if (canvas != null) {
//                canvas.dispose();
//            }
//        } catch (Exception e) {
//            System.err.println("Error during cleanup: " + e.getMessage());
//        }
//
//        System.out.println("Capture stopped.");
//    }
//
//    /**
//     * Optional method to process individual frames
//     * Can be used for saving screenshots, motion detection, etc.
//     */
//    private void processFrame(Frame frame) {
//        // Example: Save frame as image every 100 frames
//        // OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
//        // Mat mat = converter.convert(frame);
//        // imwrite("screenshot_" + System.currentTimeMillis() + ".jpg", mat);
//    }
//
//    /**
//     * Take a screenshot of the current frame
//     */
//    public void takeScreenshot() {
//        try {
//            Frame frame = grabber.grab();
//            if (frame != null) {
//                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
//                Mat mat = converter.convert(frame);
//                String filename = "tapo_screenshot_" + System.currentTimeMillis() + ".jpg";
//                imwrite(filename, mat);
//                System.out.println("Screenshot saved: " + filename);
//            }
//        } catch (Exception e) {
//            System.err.println("Error taking screenshot: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//
//        // Create and start the camera capture application
//        TapoService app = new TapoService(cameraIp, username, password);
//
//        // Add shutdown hook for clean exit
//        Runtime.getRuntime().addShutdownHook(new Thread(app::stopCapture));
//
//        // Initialize and start capture
//        app.initialize();
//        app.startCapture();
//
//        // Keep the main thread alive
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            System.out.println("Application interrupted.");
//        }
//    }

}
