package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

import java.util.Map;

public class RootController {
    public static void index(Context ctx) {
        BasePage flash = new BasePage();
        flash.setFlash(ctx.consumeSessionAttribute("flash"));
        flash.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", Map.of("page", flash));
    }
}
