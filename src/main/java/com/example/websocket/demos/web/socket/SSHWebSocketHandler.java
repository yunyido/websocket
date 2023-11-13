package com.example.websocket.demos.web.socket;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.web.socket.*;

import java.io.*;
import java.util.concurrent.CompletableFuture;

public class SSHWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect("120.55.62.191", 22);
        sshClient.authPassword("root", "Abc123456");
        Session sshSession = sshClient.startSession();
        sshSession.allocateDefaultPTY();
        Session.Shell shell = sshSession.startShell();
        session.getAttributes().put("shell", shell);
        session.getAttributes().put("ssh_session", sshSession);
        session.getAttributes().put("ssh_client", sshClient);
        CompletableFuture.runAsync(() -> {
            try {
                //shell.getLocalMaxPacketSize()
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = shell.getInputStream().read(buffer)) >= 0) {
                    String output = new String(buffer, 0, bytesRead);
                    session.sendMessage(new TextMessage(output));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Session.Shell shell = (Session.Shell) session.getAttributes().get("shell");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
        bufferedWriter.write(message.getPayload() + "\r");
        bufferedWriter.flush();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("handleTransportError");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Session sshSession = (Session) session.getAttributes().get("ssh_session");
        //关闭ssh session
        sshSession.close();
        SSHClient sshClient = (SSHClient) session.getAttributes().get("ssh_client");
        //关闭ssh 连接
        sshClient.disconnect();
        System.out.println("afterConnectionClosed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
