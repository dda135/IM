package mine.fanjh.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mine.fanjh.MainApplication;

@Component
public class IMer {
	
	private volatile static boolean isStart = false;
	
	public IMer() {
		super();
		if(isStart) {
			return;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isStart) {
					return;
				}
				isStart = true;
				MainApplication.start();
			}
		}).start();
		System.out.println("IM start!");
	}
	
	
	
}
