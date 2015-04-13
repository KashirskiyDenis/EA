package ru.library_2.bean;

import java.util.Date;
import java.util.TimerTask;

public class ScheduledTask extends TimerTask {

    @Override
    public void run() {
    	// Вписать то, что должно выполняться
    	Date now = new Date();
        System.out.println("Текущая дата и время : " + now);
    }
}
