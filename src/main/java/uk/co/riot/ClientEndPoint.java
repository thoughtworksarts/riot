package uk.co.riot;
//import javax.websocket.ClientEndpoint;
//import javax.websocket.CloseReason;
//import javax.websocket.EndpointConfig;
//import javax.websocket.OnClose;
//import javax.websocket.OnError;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;

//@ClientEndpoint
public class ClientEndPoint {
	
	public static final String SQUARE_MESSAGE = "square"; 
	public static final String TRIANGLE_MESSAGE = "triangle"; 
    
//	@OnOpen
//	public void onOpen(Session session, EndpointConfig conf) {
//		System.out.println("Connected to server.");
//	}
//	
//	@OnMessage
//	public void onMessage(String message, Session session) {
//		//System.out.println("Received message from remote: " + message);
//		MainContainer.mCurrentScreen.onRemoteMessage(message);
//	}
//	
//	@OnError
//	public void onError(Session session, Throwable error) {
//		System.out.println("Encountered an error: " + error);
//	}
//	
//	@OnClose
//	public void onClose(Session session, CloseReason reason) {
//		System.out.println("Connection with server closed. Reason: " + reason.getReasonPhrase());
//	}
}