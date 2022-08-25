package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.config.EmoteCache;
import com.dan.minecraft7tv.gui.widget.DownloadingWidget;

public class DownloadThread implements Runnable {

    private EmoteCache downloadable;

    public DownloadThread(EmoteCache cache) {
        this.downloadable = cache;
    }

    @Override
    public void run() {
        for (String e : EmoteRenderer.getInstance().getNames()) {
            if (e.equals(this.downloadable.getName())) {
                //TODO: error this name exists
                return;
            }
        }
        for (RenderableEmote emote1 : EmoteRenderer.getInstance().getEmotes()) {
            if (emote1.getEmote().getUrl().equals(this.downloadable.getUrl())) {
                return;
            }
        }
        if(!this.downloadable.getUrl().contains("https://cdn.7tv.app/")) return;
        DownloadingWidget widget = new DownloadingWidget(downloadable.getName());
        EmoteRenderer.getInstance().addDownloading(widget);
        Emote emote = new Emote(this.downloadable.getUrl(), this.downloadable.getName());
        if (null == emote.getBuffer() && emote.isGif()) {
            EmoteRenderer.getInstance().removeDownloading(new DownloadingWidget(downloadable.getName()));
            return;
        }
        EmoteRenderer.getInstance().addRenderableEmote(new RenderableEmote(emote));
        Config.getInstance().emotes.add(this.downloadable);
        widget.close();
    }
}
