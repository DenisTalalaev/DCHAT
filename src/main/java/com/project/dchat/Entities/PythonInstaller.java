package com.project.dchat.Entities;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PythonInstaller {

    public static void checkAndInstallPython() {
        String osName = System.getProperty("os.name");
        String command = "";
        int exitCode = -1;

        // Выбор команды для проверки наличия Python в зависимости от операционной системы
        if (osName.startsWith("Windows")) {
            command = "python --version";
        } else if (osName.startsWith("Mac") || osName.startsWith("Linux")) {
            command = "python3 --version";
        }

        try {
            // Проверка наличия Python
            Process process = Runtime.getRuntime().exec(command);

            // Ожидание завершения процесса
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (exitCode != 0) {
            // Python не установлен, выполняем установку
            try {
                // Загрузка установочного файла Python с официального сайта
                URL url = new URL("https://www.python.org/ftp/python/3.9.6/python-3.9.6-amd64.exe");
                Path path = Paths.get("python-3.9.6-amd64.exe");
                Files.copy(url.openStream(), path);

                // Выполнение команды для установки Python
                Process installProcess = Runtime.getRuntime().exec("cmd /c start /wait python-3.9.6-amd64.exe /quiet");

                // Ожидание завершения процесса
                installProcess.waitFor();

                // Удаление загруженного установочного файла
                Files.deleteIfExists(path);

                System.out.println("Python has been installed.");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Python is already installed.");
        }
    }

}
