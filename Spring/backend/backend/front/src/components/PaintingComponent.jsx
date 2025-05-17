import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';

const PaintingComponent = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: '',
        artist: { id: null, name: '' },
        museum: { id: null, name: '' }
    });

    const [museums, setMuseums] = useState([]);
    const [artists, setArtists] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        BackendService.retrieveAllArtists(0, 100)
            .then(resp => setArtists(resp.data.content))
            .catch(console.error);
        BackendService.retrieveAllMuseums(0, 100)
                    .then(resp => setMuseums(resp.data.content))
                    .catch(console.error);

        if (id !== '-1') {
            setLoading(true);
            BackendService.retrievePainting(id)
                .then(resp => {
                    setForm({
                        name: resp.data.name,
                        artist: resp.data.artist || { id: null, name: '' },
                        museum: resp.data.museum || { id: null, name: '' }
                    });
                })
                .catch(() => navigate('/paintings'))
                .finally(() => setLoading(false));
        }
    }, [id, navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleArtistChange = (e) => {
        const artistId = Number(e.target.value);
        const selectedArtist = artists.find(c => c.id === artistId) || { id: null, name: '' };

        setForm(prev => ({
            ...prev,
            artist: selectedArtist
        }));
    };

    const handleMuseumChange = (e) => {
            const museumId = Number(e.target.value);
            const selectedMuseum = museums.find(c => c.id === museumId) || { id: null, name: '' };

            setForm(prev => ({
                ...prev,
                museum: selectedMuseum
            }));
        };

    const onSubmit = (e) => {
        e.preventDefault();

        if (!form.name || !form.artist.id || !form.museum.id) {
            alert("Заполните все обязательные поля");
            return;
        }

        const paintingData = {
            name: form.name,
            artist: { id: form.artist.id },
            museum: { id: form.museum.id }
        };

        setLoading(true);
        if (id === '-1') {
            BackendService.createPainting(paintingData)
                .then(() => navigate('/paintings'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        } else {
            BackendService.updatePainting({...paintingData, id})
                .then(() => navigate('/paintings'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        }
    };

    return (
        <div className="m-4">
            <h3>{id === '-1' ? 'Добавить картину' : 'Изменить картину'}</h3>
            <form onSubmit={onSubmit}>
                <div className="form-group">
                    <label>Название:</label>
                    <input
                        type="text"
                        name="name"
                        className="form-control"
                        value={form.name}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    />
                </div>
                <div className="form-group mt-3">
                                    <label>Автор:</label>
                                    <select
                                        className="form-control"
                                        value={form.artist.id || ''}
                                        onChange={handleArtistChange}
                                        required
                                        disabled={loading}
                                    >
                                        <option value="">Выберите автора</option>
                                        {artists.map(artist => (
                                            <option key={artist.id} value={artist.id}>
                                                {artist.name}
                                            </option>
                                        ))}
                                    </select>
                </div>
                <div className="form-group mt-3">
                    <label>Музей:</label>
                    <select
                        className="form-control"
                        value={form.museum.id || ''}
                        onChange={handleMuseumChange}
                        required
                        disabled={loading}
                    >
                        <option value="">Выберите музей</option>
                        {museums.map(museum => (
                            <option key={museum.id} value={museum.id}>
                                {museum.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="btn-group mt-3">
                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={loading}
                    >
                        {loading ? 'Сохранение...' : 'Сохранить'}
                    </button>
                    <button
                        type="button"
                        className="btn btn-secondary ms-2"
                        onClick={() => navigate('/paintings')}
                        disabled={loading}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PaintingComponent;