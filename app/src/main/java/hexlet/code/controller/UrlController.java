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
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UrlController {
    public static void create(Context ctx) throws SQLException, MalformedURLException {
        String receivedUrl = ctx.formParam("url");
        URL url;
        try {
            url = new URL(receivedUrl);

        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        String name;
        StringBuilder str = new StringBuilder();
        str.append(url.getProtocol()).append("://").append(url.getHost());
        if (url.getPort() == -1) {
            name = String.valueOf(str);
        } else {
            name = String.valueOf(str.append(":").append(url.getPort()));
        }
        if (UrlRepository.findAllByName(name).isEmpty()) {
            Url newUrl = new Url(name);
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
        } else {
            url = new URL(receivedUrl);
            StringBuilder strDanger = new StringBuilder();
            strDanger.append(url.getProtocol()).append("://").append(url.getHost());
            if (url.getPort() == -1) {
                String.valueOf(str);
            } else {
                String.valueOf(str.append(":").append(url.getPort()));
            }
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls");
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<UrlsCheckPage> urlsCheck = UrlRepository.findAllUrlsWithChecks();
        BasePage flash = new BasePage();
        flash.setFlash(ctx.consumeSessionAttribute("flash"));
        flash.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls.jte", Map.of("list", urlsCheck, "page", flash));
    }

    public static void show(Context ctx) throws SQLException {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        List<UrlCheck> urlCheck = UrlCheckRepository.findById(urlId);
        UrlsPage page = new UrlsPage(url, urlCheck);
        BasePage flash = new BasePage();
        flash.setFlash(ctx.consumeSessionAttribute("flash"));
        flash.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("show.jte", Map.of("page", page, "flash", flash));
    }

    public static void check(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        String url = UrlRepository.findById(id).get().getName();
        HttpResponse<String> response;
        String path = "/urls/" + id;
        try {
            response = Unirest.get(url).asString();
            Integer statusCode = response.getStatus();
            String body = response.getBody();
            Document html = Jsoup.parse(body);
            String title = html.title();
            Element h1Temp = html.selectFirst("h1");
            String h1 = h1Temp == null ? null : h1Temp.text();
            Element descriptionTemp = html.selectFirst("meta[name=description]");
            String description = descriptionTemp == null ? null : descriptionTemp.attr("content");
            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description);
            urlCheck.setUrlId(id);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.redirect(path);
    }
}
