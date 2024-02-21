package hexlet.code.controller;

import io.javalin.http.Context;

public class RootController {

    public static void index(Context ctx) {
        /*BasePage flash=new BasePage();

        flash.setFlash(ctx.consumeSessionAttribute("flash"));
        flash.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", Map.of("page", flash));*/
        ctx.render("index.jte");
    }

}
