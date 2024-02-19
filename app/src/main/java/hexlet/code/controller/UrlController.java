package hexlet.code.controller;

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

        String name = ctx.formParam("url");
        URL urlParser = null;
        try {
            urlParser = new URL(name);

        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        }

        String resulrUrl;
        StringBuilder str = new StringBuilder();
        str.append(urlParser.getProtocol()).append("://").append(urlParser.getHost());
        if (urlParser.getPort() == -1) {
            resulrUrl = String.valueOf(str);
        } else {
            resulrUrl = String.valueOf(str.append(":").append(urlParser.getPort()));
        }

        if (!UrlRepository.checkUrl(resulrUrl).isEmpty()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "a-danger");
            ctx.redirect("/");
        }

        Url newUrl = new Url(resulrUrl);
        UrlRepository.save(newUrl);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    }

    public static void index(Context ctx) throws SQLException {
        List<UrlsCheckPage> urlsCheck = UrlRepository.getUrls();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.render("urls.jte", Map.of("page", urlsCheck));
    }

    public static void show(Context ctx) throws SQLException {
        Long url_id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(url_id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        List<UrlCheck> urlCheck = UrlCheckRepository.find(url_id);
        UrlsPage page = new UrlsPage(url, urlCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("show.jte", Map.of("page",page));


    }
}
