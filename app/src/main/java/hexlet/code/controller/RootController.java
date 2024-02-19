package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

public class RootController {

    public static void index(Context ctx) {
        ctx.render("index.jte");
    }

}
