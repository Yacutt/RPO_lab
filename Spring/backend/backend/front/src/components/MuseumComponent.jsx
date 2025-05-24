import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';

const MuseumComponent = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        location: '',
    });
    const [error, setError] = useState(null);

    // Загрузка данных музея при монтировании
    useEffect(() => {
        if (id !== '-1') {
            BackendService.retrieveMuseum(id)
                .then(response => {
                    setFormData(response.data);
                })
                .catch(err => {
                    setError('Не удалось загрузить данные музея');
                    console.error(err);
                });
        }
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (id === '-1') {
                await BackendService.createMuseum(formData);
            } else {
                await BackendService.updateMuseum(formData);
            }
            navigate('/museums');
        } catch (err) {
            setError('Ошибка при сохранении: ' + (err.response?.data?.message || err.message));
            console.error(err);
        }
    };

    if (error) {
        return (
            <div className="alert alert-danger m-4">
                {error}
                <button onClick={() => navigate('/museums')} className="btn btn-link">
                    Вернуться к списку музеев
                </button>
            </div>
        );
    }

    return (
        <div className="m-4">
            <h3>{id === '-1' ? 'Новый музей' : 'Редактирование музея'}</h3>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label">Название музея</label>
                    <input
                        type="text"
                        className="form-control"
                        name="name"
                        value={formData.name}
                       onChange={(e) => setFormData({...formData, name: e.target.value})}
                        required
                    />
                </div>

                <div className="mb-3">
                    <label className="form-label">Местоположение</label>
                    <input
                        type="text"
                        className="form-control"
                        name="location"
                        value={formData.location}
                        onChange={(e) => setFormData({...formData, location: e.target.value})}
                        required
                    />
                </div>
                <div className="d-flex justify-content-between">
                    <button type="submit" className="btn btn-primary">
                        Сохранить
                    </button>
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={() => navigate('/museums')}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
};

export default MuseumComponent;