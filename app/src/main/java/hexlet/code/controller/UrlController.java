package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlsCheckPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UrlController {
    public static void create(Context ctx) throws SQLException {
        String receivedUrl = ctx.formParam("url");
        URL url;
        try {
            url = new URL(receivedUrl);
            String name;
            StringBuilder str = new StringBuilder();
            str.append(url.getProtocol()).append("://").append(url.getHost());
            if (url.getPort() == -1) {
                name = String.valueOf(str);
            } else {
                name = String.valueOf(str.append(":").append(url.getPort()));
            }
            if (UrlRepository.checkUrl(name).isEmpty()) {
                Url newUrl = new Url(name);
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect("/urls");
            } else {
                url = new URL(receivedUrl);
                StringBuilder str_danger = new StringBuilder();
                str_danger.append(url.getProtocol()).append("://").append(url.getHost());
                if (url.getPort() == -1) {
                    String.valueOf(str);
                } else {
                    String.valueOf(str.append(":").append(url.getPort()));
                }
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/urls");


            }

        } catch (MalformedURLException e) {
            String name = String.format("%s://%s", "null", "null");
            if (UrlRepository.checkUrl(name).isEmpty()) {
                Url newUrl = new Url(name);
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect("/urls");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/urls");
            }
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<UrlsCheckPage> urlsCheck = UrlRepository.getUrls();
        BasePage flash = new BasePage();

        flash.setFlash(ctx.consumeSessionAttribute("flash"));
        flash.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls.jte", Map.of("list", urlsCheck, "page", flash));
    }

    public static void show(Context ctx) throws SQLException {
        Long url_id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(url_id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        List<UrlCheck> urlCheck = UrlCheckRepository.find(url_id);
        UrlsPage page = new UrlsPage(url, urlCheck);
        /*page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));*/
        ctx.render("show.jte", Map.of("page", page));
    }
}
