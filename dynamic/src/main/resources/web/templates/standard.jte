@import gg.jte.Content
@param Content headerTags
@param Content infoContext
@param Content mainContent
@param Content menu
@param Content timeZoneSelect
@param String appCopyright
@param String appDetails
@param String currentPageQuery
@param String geoCoordinates
@param String page
@param String portNumber
@param String timeNow
@param String title
<!DOCTYPE html>
<html lang="en-US">
<head>
    <title>${title}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="Author" content="Daniel Popiniuc">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    ${headerTags}
    <link rel="stylesheet" href="/web/static/standard.css">
    <script src="/web/static/sidebar.js" defer></script>
    <!-- Fontawesome components -->
    <link rel="stylesheet" href="/web/static/FontAwesome/7.3.1/fontawesome.min.css">
    <script src="/web/static/FontAwesome/7.3.1/brands.min.js" defer></script>
    <script src="/web/static/FontAwesome/7.3.1/fontawesome.min.js" defer></script>
    <script src="/web/static/FontAwesome/7.3.1/regular.min.js" defer></script>
    <script src="/web/static/FontAwesome/7.3.1/solid.min.js" defer></script>
    <!-- Tabber components -->
    <link rel="stylesheet" href="/web/static/Tabber/1.0.0/tabber.css">
    <script src="/web/static/Tabber/1.0.0/tabber.min.js" defer></script>
</head>
<body>
    <style type="text/css">.tabber{display:none;}</style>
    <header id="TitleContainer">
        <h1 id="PageTitle">${title}</h1>
        <span id="portNo" class="portNoClass">${portNumber}</span>
        <span id="now" class="nowClass">${timeNow}</span>
        <a href="javascript:void(0);" id="openBtn" class="openbtn" onclick="openNav()">
            <i class="fa-solid fa-ellipsis"></i>
        </a>
        <div class="infoContextClass blueShaddowBorderLeft" id="infoContextId">${infoContext}</div>
    </header>
    <nav id="mySidebar" class="sidebar">
        <a href="javascript:void(0);" id="closeBtn" class="closebtn" onclick="closeNav()">
            <i class="fa-solid fa-ellipsis-vertical"></i>
        </a>
        <ul>${menu}</ul>
    </nav>
    <main id="main">
        ${mainContent}
    </main>
    <footer id="footer">
        <form action="/" method="get" id="TZform" class="timeZoneFormClass">
            <a href="https://maps.google.com/maps?q=${geoCoordinates}" target="_blank" style="margin-right:5px;">
                <i class="fa-solid fa-map-location"></i>
            </a>
            ${timeZoneSelect}
            <input type="hidden" name="page" value="Localization">
            <input type="hidden" name="redirectAction" value="${currentPageQuery}">
        </form>
        <span id="appDetails" class="appDetails">${appDetails}</span>
        <span id="appCopyright" class="appCopyright">${appCopyright}</span>
    </footer>
</body>
</html>