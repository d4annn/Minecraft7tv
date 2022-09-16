package com.dan.minecraft7tv.client.emote;

import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.client.gui.widget.DownloadingWidget;
import com.dan.minecraft7tv.common.EmoteCache;

public class DownloadThread implements Runnable {

    private EmoteCache downloadable;
    private boolean server;

    public DownloadThread(EmoteCache cache, boolean server) {
        this.downloadable = cache;
        this.server = server;
    }

    @Override
    public void run() {
        for (String e : EmoteRenderer.getInstance().getNames()) {
            if (e.equals(this.downloadable.getName())) {
                return;
            }
        }
        for (RenderableEmote emote1 : EmoteRenderer.getInstance().getEmotes()) {
            if (emote1.getEmote().getUrl().equals(this.downloadable.getUrl())) {
                return;
            }
        }
        if (!this.downloadable.getUrl().contains("https://cdn.7tv.app/")) return;
        DownloadingWidget widget = new DownloadingWidget(downloadable.getName());
        EmoteRenderer.getInstance().addDownloading(widget);
        Emote emote;
        emote = server ? new ServerEmote(downloadable.getUrl(), downloadable.getName()) : new Emote(downloadable.getUrl(), downloadable.getName());
        if (null == emote.getBuffer() && emote.isGif()) {
            EmoteRenderer.getInstance().removeDownloading(new DownloadingWidget(downloadable.getName()));
            return;
        }
        EmoteRenderer.getInstance().addRenderableEmote(new RenderableEmote(emote));
        if (Config.getInstance().addServerEmotes || !server) {

            if(!Config.getInstance().contains(this.downloadable.setUrl(emote.getUrl()))) {
                Config.getInstance().emotes.add(this.downloadable.setUrl(emote.getUrl()));
            }
        }
        widget.close();
        Config.getInstance().saveConfig();
    }
}
