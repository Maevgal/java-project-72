@import hexlet.code.dto.UrlsCheckPage
@import java.sql.Timestamp
@import java.time.format.DateTimeFormatter
@import java.util.List
@import java.util.Optional
@param List<UrlsCheckPage> page
@template.layout.page(
content = @`
    <html>
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
                    @for (var url :page)
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
                                <a>${Optional.ofNullable(url.getStatus_code())
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
    </html>
`)