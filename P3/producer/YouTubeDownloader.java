package producer;

import java.io.*;

public class YouTubeDownloader {

    public static void downloadYouTubeVideos(String savePath) {
        // Example YouTube URLs (Replace these with actual links)
        String[] youtubeLinks = {
            "https://www.youtube.com/watch?v=rGPd0FyjQ9w",
            "https://www.youtube.com/watch?v=xItEHx7aI-4",
            "https://www.youtube.com/watch?v=_EfY8OeQ-CY",
            "https://www.youtube.com/watch?v=ONUMCQSNlS0",
            "https://www.youtube.com/watch?v=PjhdKY0hyCA",
            "https://www.youtube.com/watch?v=zPmSwXMQtRk",
            "https://www.youtube.com/watch?v=oRhBc9bqGnY",
            "https://www.youtube.com/watch?v=zUzk8Z-ZUWM",
            "https://www.youtube.com/watch?v=erYO3TOX5jU",
            "https://www.youtube.com/watch?v=C4zhfOcyhro",
            "https://www.youtube.com/watch?v=kB-qZhIdxKA"
        };

        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs(); // Ensure producer thread folder exists
        }

        for (String videoUrl : youtubeLinks) {
            downloadYouTubeVideo(videoUrl, savePath);
        }
    }

    private static void downloadYouTubeVideo(String videoUrl, String savePath) {
        try {
            // yt-dlp command to download MP4 format and limit duration to 5 minutes
            String command = "yt-dlp --cookies cookies.txt " +
                         "-f \"136+140/best[ext=mp4]\" " +  // 136 (720p) + 140 (m4a audio)
                         "--max-filesize 50M " + 
                         "-o \"" + savePath + "/%(title)s.%(ext)s\" " + 
                         "\"" + videoUrl + "\"";

            System.out.println("Downloading: " + videoUrl + " to " + savePath);
            Process process = Runtime.getRuntime().exec(command);

            // Capture output for debugging
            printProcessOutput(process);

            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Download completed: " + videoUrl);
            } else {
                System.out.println("Download failed: " + videoUrl);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print process output
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line); // Print process errors
            }
        }
    }
}
