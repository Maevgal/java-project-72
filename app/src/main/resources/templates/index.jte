<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/application.html}">

<section layout:fragment="content">
    <div class="container-fluid bg-dark text-white">
            <h1>Анализатор страниц</h1>
            <p>Бесплатно проверяйте сайты на SEO пригодность<p>
            <form class="d-flex">
                <input class="form-control me-2" type="search" placeholder="Ссылка" aria-label="Search">
                <button class="btn btn-primary" type="submit">Проверить</button>
            </form>
            <small class="text-muted">Пример: https://www.example.com</small>
        </div>
    </div>
</section>
</html>
