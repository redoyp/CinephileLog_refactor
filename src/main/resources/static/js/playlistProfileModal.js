function openProfilePlaylistModal() {
    const modal = new bootstrap.Modal(document.getElementById('profilePlaylistModal'));
    modal.show();
    loadPlaylistsForProfile();
}

function loadPlaylistsForProfile() {
    fetch('/playlists')
        .then(res => res.json())
        .then(data => {
            const list = $('#profilePlaylistList');
            list.empty();

            data.forEach(pl => {
                list.append(`
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span style="cursor:pointer;" onclick="loadProfilePlaylistMovies(${pl.playlistId}, '${pl.name}')">${pl.name}</span>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteProfilePlaylist(${pl.playlistId})">🗑</button>
                    </li>
                `);
            });

            $('#myPlaylists').show();
        });
}

function loadProfilePlaylistMovies(playlistId, name) {
    fetch(`/playlists/${playlistId}`)
        .then(res => res.json())
        .then(data => {
            console.log('받은 영화 목록:', data.movies);

            $('#profilePlaylistTitle').text(`🎞 ${name}`);
            const container = $('#profilePlaylistMovies'); // ← 여기 수정
            container.empty();

            data.movies.forEach(m => {
                const posterUrl = m.posterUrl?.startsWith('/') ? m.posterUrl : '/' + m.posterUrl;

                container.append(`
                    <div class="col-md-3 mb-3">
                        <div class="card h-100">
                            <a href="/movieDetail/${m.movieId}">
                                <img src="https://image.tmdb.org/t/p/w500${posterUrl}" class="card-img-top" alt="${m.title}">
                            </a>
                            <div class="card-body p-2 d-flex justify-content-between align-items-center">
                                <span class="small text-truncate" style="max-width: 80%;">${m.title}</span>
                                <button class="btn btn-sm btn-danger" onclick="removeProfileMovie(${playlistId}, ${m.movieId})">✖</button>
                            </div>
                        </div>
                    </div>
                `);
            });

            $('#profilePlaylistDetail').show();
        })
        .catch(err => {
            console.error('영화 목록 불러오기 실패:', err);
        });
}

function removeProfileMovie(playlistId, movieId) {
    fetch(`/playlists/${playlistId}/movies/${movieId}`, { method: 'DELETE' })
        .then(() => loadProfilePlaylistMovies(playlistId));
}

function deleteProfilePlaylist(playlistId) {
    if (confirm('정말 이 플레이리스트를 삭제하시겠습니까?')) {
        fetch(`/playlists/${playlistId}`, { method: 'DELETE' })
            .then(() => {
                loadPlaylistsForProfile();
                $('#profilePlaylistDetail').hide();
            });
    }
}

function toggleCreateProfilePlaylistForm() {
    $('#createProfilePlaylistForm').toggle();
}

function createProfilePlaylist() {
    const name = $('#profileNewPlaylistName').val();
    const description = $('#profileNewPlaylistDesc').val();

    if (!name.trim()) {
        alert("리스트 이름을 입력해주세요.");
        return;
    }

    fetch('/playlists', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ name, description })
    })
        .then(res => res.json())
        .then(() => {
            $('#profileNewPlaylistName').val('');
            $('#profileNewPlaylistDesc').val('');
            $('#createProfilePlaylistForm').hide();
            loadPlaylistsForProfile();
        })
        .catch(err => {
            console.error('플레이리스트 생성 실패:', err);
            alert("생성 중 오류가 발생했습니다.");
        });
}

