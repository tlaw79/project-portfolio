$(document).ready(function() {
    document.getElementById("nav").innerHTML =
        '<div id="topLeftCorner"></div>' + '<ul>' +
            '<li><form action="index.html"><input type="submit" value="Keepe Of The Dungeon Pit"></form></li>' +
            '<li><form action="RockAtomicRecords.html"><input type="submit" value="Rock Atomic Records"></form></li>' +
            '<li>' +
                 '<div class="dropdown">' +
                    '<button onclick="showContent()" class="dropbtn">Classic Cutz</button>' +
                    '<div id="navDropdown" class="dropdown-content">' +
                        '<a href="#">Link 1</a>' +
                        '<a href="#">Link 2</a>' +
                        '<a href="#">Link 3</a>' +
                    '</div>' +
                '</div>' +
            '</li>' +
            '<li><form action="WastelandRadio.html"><input type="submit" value="Wasteland Radio"></form></li>' +
            '<li><form action="about.html"><input type="submit" value="About"></form></li>' +
            '<li><form action="links.html"><input type="submit" value="Links"></form></li>' +
            '<li><form action="complaints.html"><input type="submit" value="Complaints"></form></li>' +
        '</ul>';
});
