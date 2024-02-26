@import hexlet.code.dto.UrlsCheckPage
@import java.sql.Timestamp
@import java.time.format.DateTimeFormatter
@import java.util.List
@import hexlet.code.dto.BasePage
@import java.util.Optional
@param List<UrlsCheckPage> list
@param BasePage page
@template.layout.page(
content = @`
    <html>
    <section>
    @if(page != null && page.getFlash() != null)
         <div class="rounded-0 m-0 alert alert-dismissible fade show alert alert-${page.getFlashType()}" role="alert">
            <p class="m-0">${page.getFlash()}</p>
             <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    <main>
        <section>
            <div class="container-lg mt-5">
                <h1>Сайты</h1>

                <table class="table table-bordered table-hover mt-3">
                    <thead>
                    <tr>
                        <th class="col-1">ID</th>
                        <th>Имя</th>
                        <th class="col-2">Последняя проверка</th>
                        <th class="col-1">Код ответа</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for (var url :list)
                        <tr>
                            <td>${url.getId()}</td>
                            <td>
                                <a href="/urls/${url.getId()}">${url.getUrl()}</a>
                            </td>
                            <td>
                                !{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
                                ${Optional.ofNullable(url.getCheck())
                                    .map(Timestamp::toLocalDateTime)
                                    .map(date-> date.format(formatter))
                                    .orElse(" ")}
                            </td>
                            <td>
                                <a>${Optional.ofNullable(url.getStatusCode())
                                        .map(String::valueOf)
                                        .orElse(" ")}</a>
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </section>
    </main>
    </section>
    </html>
`)