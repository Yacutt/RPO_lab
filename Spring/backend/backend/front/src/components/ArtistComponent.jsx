import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';

const ArtistComponent = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: '',
        century: '',
        country: { id: null, name: '' }
    });

    const [countries, setCountries] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        BackendService.retrieveAllCountries(0, 100)
            .then(resp => setCountries(resp.data.content))
            .catch(console.error);

        if (id !== '-1') {
            setLoading(true);
            BackendService.retrieveArtist(id)
                .then(resp => {
                    setForm({
                        name: resp.data.name,
                        century: resp.data.century,
                        country: resp.data.country || { id: null, name: '' }
                    });
                })
                .catch(() => navigate('/artists'))
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

    const handleCountryChange = (e) => {
        const countryId = Number(e.target.value);
        const selectedCountry = countries.find(c => c.id === countryId) || { id: null, name: '' };

        setForm(prev => ({
            ...prev,
            country: selectedCountry
        }));
    };

    const onSubmit = (e) => {
        e.preventDefault();

        if (!form.name || !form.century || !form.country.id) {
            alert("Заполните все обязательные поля");
            return;
        }

        const artistData = {
            name: form.name,
            century: form.century,
            country: { id: form.country.id }
        };

        setLoading(true);
        if (id === '-1') {
            BackendService.createArtist(artistData)
                .then(() => navigate('/artists'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        } else {
            BackendService.updateArtist({...artistData, id})
                .then(() => navigate('/artists'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        }
    };

    return (
        <div className="m-4">
            <h3>{id === '-1' ? 'Добавить художника' : 'Изменить художника'}</h3>
            <form onSubmit={onSubmit}>
                <div className="form-group">
                    <label>Имя:</label>
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
                    <label>Век:</label>
                    <input
                        type="text"
                        name="century"
                        className="form-control"
                        value={form.century}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    />
                </div>

                <div className="form-group mt-3">
                    <label>Страна:</label>
                    <select
                        className="form-control"
                        value={form.country.id || ''}
                        onChange={handleCountryChange}
                        required
                        disabled={loading}
                    >
                        <option value="">Выберите страну</option>
                        {countries.map(country => (
                            <option key={country.id} value={country.id}>
                                {country.name}
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
                        onClick={() => navigate('/artists')}
                        disabled={loading}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ArtistComponent;