package com.learnspigot.bot.other.command.wixstock;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.awt.*;
import java.io.IOException;

public final class WixStockCommand {
    @Command(label = "wixstock", usage = "/wixstock", description = "Realtime stock information for Wix.", log = true)
    public void onWixStockCommand(final @NotNull CommandInfo info) {
        StockQuote wixStock = null;
        try {
            wixStock = YahooFinance.get("WIX").getQuote(true);
        } catch (IOException ignored) {
        }

        if (wixStock == null) {
            info.event().replyEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Unable to fetch Wix stock information.")
                    .build()
            ).queue();
            return;
        }

        info.event().replyEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Wix Stock")
                .addField("Current Value (USD)", "$" + wixStock.getPrice(), false)
                .build()
        ).queue();
    }
}
