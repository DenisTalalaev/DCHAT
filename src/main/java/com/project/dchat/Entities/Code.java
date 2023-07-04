package com.project.dchat.Entities;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Code {
    private String code;
    public Code(String code) {
        code = code.replace("<code>", "");
        code = code.replace("</code>", "");
        this.code = code;
    }

    public Code(byte[] data) {
        this.code = new String(data);
    }

    public String getCode() {
        return code;
    }

    public String executePythonCode() {
        String result = "";
        String line;

        try {
            Process process = Runtime.getRuntime().exec("python -");

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            process.getOutputStream().write(code.getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();

            while ((line = input.readLine()) != null) {
                result += line + "\n";
            }

            result += "\n";

            while ((line = error.readLine()) != null) {
                result += line + "\n";
            }

            input.close();
            error.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static void main(String[] args) {
        Code code = new Code("for i in range(45): print(\"hello world\")");
        System.out.println(code.executePythonCode());
    }

}

