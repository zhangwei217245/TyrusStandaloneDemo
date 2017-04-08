/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package x.spirit.tyrusdemo.client;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;

/**
 *
 * @author zhangwei
 */
public class StringClient {
    private static CountDownLatch messageLatch;
    private static CountDownLatch recvLatch;
    private static final String SENT_MESSAGE = "ACK";
    private static Timer timer;

    public static void main(String [] args){
        try {
            String wsAddr = "ws://localhost:8080/websockets/StringEndPoint";
            
            messageLatch = new CountDownLatch(10);
            recvLatch = new CountDownLatch(10);
            timer = new Timer();

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            client.asyncConnectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {

                            @Override
                            public void onMessage(String message) {
                                    System.out.println("[CLIENT RECV] Received message: "+message);
                                    
                                    String msg = "Message " + SENT_MESSAGE + "_" + message.split(",")[1];
                                    session.getAsyncRemote().sendText(msg);
                                    System.out.println("[CLIENT SEND] " + msg);
                                    recvLatch.countDown();
                            }
                        });
                        session.getBasicRemote().sendText("GET");
//                        timer.scheduleAtFixedRate(new TimerTask() {
//                            public void run() {
//                                try {
//                                    String msg = "Message " + SENT_MESSAGE + "_" + UUID.randomUUID();
//                                    System.out.println("[CLIENT SEND] " + msg);
//                                    session.getBasicRemote().sendText(msg);
//                                    messageLatch.countDown();
//                                } catch (IOException ex) {
//                                    System.err.println(ex.getMessage());
//                                }
//                            }
//                        }, 0, 1000);
                        
//                        session.getBasicRemote().sendText("STOP");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        Logger.getLogger(StringClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                
                
            }, cec, new URI(wsAddr));
            
            recvLatch.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
