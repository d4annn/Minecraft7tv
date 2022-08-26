package com.dan.minecraft7tv.client.gui.widget;

public class ProgressBar {

    private int start;
    private int current;
    private int end;

    public ProgressBar(int start, int end) {
        this.start = start;
        this.end = end;
        this.current = start;
    }

    public ProgressBar() {
        this.start = 0;
        this.end = 100;
        this.current = 0;
    }

public boolean isGreen(){
        float each=(this.end-this.start)/3f;
        return this.current>=each*2+this.start;
        }

public boolean isYellow(){
        return this.current>=(this.end-this.start)/3+this.start;
        }

public boolean isRed(){
        return this.current<=(this.end-this.start)/3+this.start;
        }

public int getStart(){
        return start;
        }

public void setStart(int start){
        this.start=start;
        }

public int getEnd(){
        return end;
        }

public void setEnd(int end){
        this.end=end;
        }
        }
