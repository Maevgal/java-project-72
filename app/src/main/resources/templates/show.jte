@import hexlet.code.dto.UrlsPage
@import hexlet.code.dto.BasePage
@param BasePage flash
@param UrlsPage page


@template.layout.page(
content = @`
<html>
<main class="flex-grow-1">

    <section>
        @if(page != null && flash.getFlash() != null)
            <div class="rounded-0 m-0 alert alert-dismissible fade show alert alert-${flash.getFlashType()}" role="alert">
                <p class="m-0">${flash.getFlash()}</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        <div class="container-lg mt-5">
            <h1>${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                <tbody>

                <tr>
                    <td>ID</td>
                    <td>${page.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${page.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>
                        !{var formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
                        ${page.getUrl().getCreatedAt().toLocalDateTime().format(formatter)}
                    </td>
                </tr>

                </tbody>
            </table>

            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="/urls/${page.getUrl().getId()}/checks">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th class="col-2">Дата проверки</th>
                </thead>
                <tbody>
                @for(var urlCheck : page.getUrlChecks())
                <tr>
                    <td>${urlCheck.getId()}</td>
                    <td>${urlCheck.getStatusCode()}</td>
                    <td>${urlCheck.getTitle()}</td>
                    <td>${urlCheck.getH1()}</td>
                    <td>${urlCheck.getDescription()}</td>
                    <td>${urlCheck.getCreatedAt().toLocalDateTime().format(formatter)}</td>
                </tr>
                @endfor
                </tbody>
            </table>
        </div>

    </section>
</main>
</html>
`)