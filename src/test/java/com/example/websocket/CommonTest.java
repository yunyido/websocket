package com.example.websocket;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CommonTest {
    private static final Console con = System.console();


//    @Test
    public void test() throws IOException {
        final SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect("120.55.51.38", 22);
        ssh.authPassword("root", "Abc123456");
        Session session = null;
        try {
            session = ssh.startSession();
            session.allocateDefaultPTY();
            Session.Shell shell = session.startShell();
            new StreamCopier(shell.getInputStream(), System.out, LoggerFactory.DEFAULT)
                    .bufSize(shell.getLocalMaxPacketSize())
                    .spawn("stdout");
            new StreamCopier(shell.getErrorStream(), System.err, LoggerFactory.DEFAULT)
                    .bufSize(shell.getLocalMaxPacketSize())
                    .spawn("stderr");
            new StreamCopier(System.in, shell.getOutputStream(), LoggerFactory.DEFAULT)
                    .bufSize(shell.getRemoteMaxPacketSize())
                    .keepFlushing(true)
                    .copy();

//            session.getOutputStream().write("ls /".getBytes(Charset.defaultCharset()));
//            session.getOutputStream().flush();
//            Session finalSession = session;
//            CompletableFuture.runAsync(() -> {
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(finalSession.getInputStream()));
//                String line;
//                while (true) {
//                    try {
//                        if ((line = bufferedReader.readLine()) == null) break;
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("cmd:" + line);
//                }
//            });


//            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                // Do Nothing
            }

            ssh.disconnect();
        }

    }

    private void exec(Session session, String command) throws IOException {
        Session.Shell shell = session.startShell();

        shell.getOutputStream().write(command.getBytes());
        shell.getOutputStream().flush();


//        final Session.Command cmd = session.exec(command);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
    }

//    @Test
    public void test2() throws IOException, JSchException, InterruptedException {
        JSch jSch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PasswordAuthentication", "yes");
        config.put("userauth.gssapi-with-mic", "no");
        com.jcraft.jsch.Session session = jSch.getSession("root", "120.55.51.38", 22);
        session.setConfig(config);
        session.setPassword("Abc123456");
        session.connect(3000);
        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        channel.setPtySize(80, 24, 640, 480);
        channel.connect(3000);

//        OutputStream outputStream = channel.getOutputStream();
//        outputStream.write("ls /".getBytes(StandardCharsets.UTF_8));
//        outputStream.flush();

        CompletableFuture.runAsync(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("cmd:" + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(5000);

    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        var s = "hello vr";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(new URI("https://www.yun.shop/console/api/v1/welcome/info"))
                .setHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiLlpJrlpJrnlKjmiLc3MzgyIiwic2NvcGUiOiIiLCJpc3MiOiJzZWxmIiwiZXhwIjoxNjk4Nzc1NDQxLCJpYXQiOjE2OTg3NDY2NDEsInVzZXJJZCI6IjQ4ZDU3YzM5NGYxNDAyOTNmMzU4ZTEzZDZlZDZkMmEwIn0.XuSMCrW1KHmeLa-x2RtR-bjeZSBphWcOwWSL1r9nUGVk_96kn39ypQxDq1OZc6gm8xzrn5AokojKBLK5YLD1C0OfKen8fj0r1dh-8nUNCTvDfDN0pkA54P7_QCo7YA9pA3af43NT_OK9ueHfDTnbXlkzMjFjHqrKf5J9fWXvjTXhUjxg8asbQ8hvGTYtYOCi7rHKtAI-3uJCweCMJU4yVCY51EPshqprUuznkTMFdwowx4ufiOzLIHgS48AZ1v8cw6-sUbhme85pVSOQx7p9BQfNvn3UXA_Gn6xgoI2TylA__3CytgkMydl7EeqEnIeZqjML9W3MRYYuS4ht6acGMg")
                .build();

        HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());

        String a = """
                {
                    "name":"",
                    "age":2
                }
                           
                """;

        System.out.println(s);
        String c = "b";
        switch (c) {
            case "a" -> System.out.println("a");
            case "b" -> System.out.println("b");
            case "c" -> System.out.println("c");
            default -> System.out.println("default");
        }

        User as = new User("as", "");

        List<String> list1 = List.of("a", "b", "c");
        List<String> list2 = Stream.of("a", "b", "c").toList();


    }

    record User(String name, String phone) {
    }
}
