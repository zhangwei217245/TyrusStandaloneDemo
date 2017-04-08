/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package x.spirit.tyrusdemo.websocket;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Open your web browser, open this link: 
 * http://www.websocket.org/echo.html
 * 
 * And if you see "This web browser support WebSocket", which means you can test
 * this websocket on this web page now. 
 * 
 * In the location part, input
 * ws://localhost:8080/websockets/StringEndPoint
 * 
 * In the message box, type:
 * 
 * Test WebSocket
 * 
 * Press send button, you should be able to see your message in your console. 
 * 
 * In the message box, type:
 * 
 * GET
 * 
 * Press send button, you should be able to see the random messages sent by your
 * java program to the web browser. 
 * 
 * @author zhangwei
 */
@ServerEndpoint("/StringEndPoint")
public class StringEndPoint {
    private Timer timer;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[SERVER] Open session " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, final Session session) {
        System.out.println("[SERVER RECV] Session " + session.getId() + " message: " + message);
        if ("GET".equals(message)) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    try {
                        String msg = "Message " + UUID.randomUUID();
                        System.out.println("[SERVER SEND] " + msg);
                        session.getBasicRemote().sendText(msg);
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }, 0, 1000);
        } else if ("STOP".equals(message)) {
            timer.cancel();
        }
    }

    @OnClose
    public void onClose(Session session) {
        timer.cancel();
        System.out.println("[SERVER] Session " + session.getId() + " is closed.");
    }

}
