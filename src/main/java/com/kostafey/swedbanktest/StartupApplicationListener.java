package com.kostafey.swedbanktest;

import com.kostafey.swedbanktest.db.InitDB;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  @Override public void onApplicationEvent(ContextRefreshedEvent event) {
      InitDB.createDB();
      InitDB.writeData();
  }
}