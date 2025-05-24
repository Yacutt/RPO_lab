import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';

const CountryComponent = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [name, setName] = useState('');

    useEffect(() => {
        if (id !== '-1') {
            BackendService.retrieveCountry(id)
                .then(resp => setName(resp.data.name))
                .catch(() => navigate('/countries'));
        }
    }, [id, navigate]);

    const onSubmit = (e) => {
        e.preventDefault();
        const country = { name };

        if (id === '-1') {
            BackendService.createCountry(country)
                .then(() => navigate('/countries'));
        } else {
            BackendService.updateCountry({...country, id})
                .then(() => navigate('/countries'));
        }
    };

    return (
        <div className="m-4">
            <h3>{id === '-1' ? 'Добавить страну' : 'Изменить страну'}</h3>
            <form onSubmit={onSubmit}>
                <div className="form-group">
                    <label>Название:</label>
                    <input
                        type="text"
                        className="form-control"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div className="btn-group mt-3">
                    <button type="submit" className="btn btn-primary">
                        Сохранить
                    </button>
                    <button
                        type="button"
                        className="btn btn-secondary ms-2"
                        onClick={() => navigate('/countries')}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CountryComponent;