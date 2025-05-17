import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';

const UserComponent = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        login: '',
        email: ''
    });

    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        BackendService.retrieveAllUsers(0, 100)
            .then(resp => setUsers(resp.data.content))
            .catch(console.error);

        if (id !== '-1') {
            setLoading(true);
            BackendService.retrieveUser(id)
                .then(resp => {
                    setForm({
                        login: resp.data.login,
                        email: resp.data.email
                    });
                })
                .catch(() => navigate('/users'))
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


    const onSubmit = (e) => {
        e.preventDefault();

        if (!form.login || !form.email) {
            alert("Заполните все обязательные поля");
            return;
        }

        const userData = {
            login: form.login,
            email: form.email
        };

        setLoading(true);
        if (id === '-1') {
            BackendService.createUser(userData)
                .then(() => navigate('/users'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        } else {
            BackendService.updateUser({...userData, id})
                .then(() => navigate('/users'))
                .catch(error => alert(`Ошибка: ${error.message}`))
                .finally(() => setLoading(false));
        }
    };

    return (
        <div className="m-4">
            <h3>{id === '-1' ? 'Добавить пользователя' : 'Изменить пользователя'}</h3>
            <form onSubmit={onSubmit}>
                <div className="form-group">
                    <label>Логин:</label>
                    <input
                        type="text"
                        name="login"
                        className="form-control"
                        value={form.login}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    />
                </div>

                <div className="form-group mt-3">
                    <label>Email:</label>
                    <input
                        type="text"
                        name="email"
                        className="form-control"
                        value={form.email}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    />
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
                        onClick={() => navigate('/users')}
                        disabled={loading}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
};

export default UserComponent;