let currentMovieId = null;

function showCreatePlaylistForm() {
    $('#createPlaylistForm').toggle();
}

function createPlaylist() {
    const name = $('#newPlaylistName').val();
    const description = $('#newPlaylistDesc').val();

    fetch('/playlists', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name, description})
    })
        .then(res => res.json())
        .then(() => {
            loadMyPlaylists();
            $('#newPlaylistName').val('');
            $('#newPlaylistDesc').val('');
            $('#createPlaylistForm').hide();
        });
}

function loadMyPlaylists() {
    fetch('/playlists')
        .then(res => res.json())
        .then(data => {
            const list = $('#playlistList');
            list.empty();
            data.forEach(pl => {
                list.append(`
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span style="cursor:pointer;" onclick="loadPlaylistMovies(${pl.playlistId}, '${pl.name}')">${pl.name}</span>
                        ${currentMovieId !== null ? `<button class="btn btn-sm btn-outline-success" onclick="addMovie(${pl.playlistId})">➕</button>` : ''}
                        <button class="btn btn-sm btn-outline-danger ms-2" onclick="deletePlaylist(${pl.playlistId})">🗑</button>
                    </li>
                `);
            });
        });
}

function addMovie(playlistId) {
    fetch(`/playlists/${playlistId}/movies`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({movieId: currentMovieId})
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(msg => { throw new Error(msg); });
            }
            return res;
        })
        .then(() => {
            alert('영화가 추가되었습니다!');
        })
        .catch(err => {
            alert(err.message);
        });
}


function loadPlaylistMovies(playlistId, name) {
    fetch(`/playlists/${playlistId}`)
        .then(res => res.json())
        .then(data => {
            $('#playlistDetailTitle').text(`🎞 ${name}`);
            const list = $('#playlistMovieList');
            list.empty();

            data.movies.forEach(m => {
                const posterUrl = m.posterUrl?.startsWith('/') ? m.posterUrl : '/' + m.posterUrl;

                list.append(`
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <a href="/movieDetail/${m.movieId}" class="d-flex align-items-center" style="text-decoration: none; color: inherit;">
                            <img src="https://image.tmdb.org/t/p/w500${posterUrl}" alt="${m.title}" style="width: 40px; height: auto; margin-right: 10px;">
                            ${m.title}
                        </a>
                        <button class="btn btn-sm btn-danger" onclick="removeMovie(${playlistId}, ${m.movieId})">✖</button>
                    </li>
                `);
            });

            $('#playlistDetail').show();
        });
}


function removeMovie(playlistId, movieId) {
    fetch(`/playlists/${playlistId}/movies/${movieId}`, { method: 'DELETE' })
        .then(() => loadPlaylistMovies(playlistId));
}

function deletePlaylist(playlistId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        fetch(`/playlists/${playlistId}`, { method: 'DELETE' })
            .then(() => {
                loadMyPlaylists();
                loadMyPlaylistsForProfile();
                $('#profilePlaylistDetail').hide();
            });
    }
}

function loadMyPlaylistsForProfile() {
    fetch('/playlists')
        .then(res => res.json())
        .then(data => {
            const list = $('#profilePlaylistList');
            list.empty();
            data.forEach(pl => {
                list.append(`
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span style="cursor:pointer;" onclick="loadPlaylistMoviesForProfile(${pl.playlistId}, '${pl.name}')">${pl.name}</span>
                        <button class="btn btn-sm btn-danger" onclick="deletePlaylist(${pl.playlistId})">🗑</button>
                    </li>
                `);
            });
        });
}

function loadPlaylistMoviesForProfile(playlistId, name) {
    fetch(`/playlists/${playlistId}`)
        .then(res => res.json())
        .then(data => {
            $('#profilePlaylistTitle').text(`🎞 ${name} (${data.movies.length}편)`);
            const container = $('#playlistMovieBox');
            container.empty();

            data.movies.forEach(m => {
                container.append(`
                    <div class="col-md-3 mb-3">
                        <div class="card">
                            <a href="/movieDetail/${m.movieId}">
                                <img src="https://image.tmdb.org/t/p/w500${m.posterUrl}" class="card-img-top" alt="${m.title}">
                            </a>
                            <div class="card-body p-2">
                                <h6 class="card-title text-truncate" title="${m.title}">${m.title}</h6>
                                <button class="btn btn-sm btn-outline-danger w-100" onclick="removeMovie(${playlistId}, ${m.movieId})">삭제</button>
                            </div>
                        </div>
                    </div>
                `);
            });

            $('#profilePlaylistDetail').show();
        });
}
