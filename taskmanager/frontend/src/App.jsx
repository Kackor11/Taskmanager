import React, { useState, useEffect } from 'react';
import { useAuth } from "react-oidc-context";

export default function App() {
    const auth = useAuth();
    const [error, setError] = useState('');

    const [meetings, setMeetings] = useState([]);
    const [meetingTitle, setMeetingTitle] = useState('');
    const [meetingDesc, setMeetingDesc] = useState('');

    const [tasks, setTasks] = useState([]);
    const [taskTitle, setTaskTitle] = useState('');

    // Funkcja generująca bezpieczne nagłówki z tokenem JWT
    const getHeaders = () => {
        return {
            'Content-Type': 'application/json',
            'Authorization': auth.user ? `Bearer ${auth.user.access_token}` : ''
        };
    };

    const fetchMeetings = async () => {
        if (!auth.isAuthenticated) return;
        try {
            const res = await fetch(`/api/meetings`, { headers: getHeaders() });
            if (!res.ok) throw new Error("Błąd pobierania spotkań");
            const data = await res.json();
            setMeetings(Array.isArray(data) ? data : []);
        } catch (err) { console.error(err); }
    };

    const fetchTasks = async () => {
        if (!auth.isAuthenticated) return;
        try {
            const res = await fetch('/api/tasks', { headers: getHeaders() });
            if (!res.ok) throw new Error("Błąd pobierania zadań");
            const data = await res.json();
            setTasks(Array.isArray(data) ? data : []);
        } catch (err) { console.error(err); }
    };

    useEffect(() => {
        if (auth.isAuthenticated) {
            fetchMeetings();
            fetchTasks();
        }
    }, [auth.isAuthenticated]);

    const addMeeting = async (e) => {
        e.preventDefault();
        const userId = auth.user.profile.sub;
        await fetch('/api/meetings', {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({ title: meetingTitle, description: meetingDesc, userId: userId })
        });
        setMeetingTitle(''); setMeetingDesc(''); await fetchMeetings();
    };

    const deleteMeeting = async (id) => {
        await fetch(`/api/meetings/${id}`, { method: 'DELETE', headers: getHeaders() });
        await fetchMeetings();
    };

    const addTask = async (e) => {
        e.preventDefault();
        await fetch('/api/tasks', {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({ title: taskTitle, completed: false })
        });
        setTaskTitle(''); await fetchTasks();
    };

    const deleteTask = async (id) => {
        const res = await fetch(`/api/tasks/${id}`, { method: 'DELETE', headers: getHeaders() });
        if(res.status === 403) {
            alert("Brak uprawnień! Tylko ADMIN może usuwać zadania.");
        }
        await fetchTasks();
    };

    // EKRAN LOGOWANIA (Tylko jeden przycisk - PKCE)
    if (auth.isLoading) {
        return <div style={{ padding: '50px', textAlign: 'center' }}>Ładowanie...</div>;
    }

    if (auth.error) {
        return <div style={{ padding: '50px', color: 'red' }}>Błąd autoryzacji: {auth.error.message}</div>;
    }

    if (!auth.isAuthenticated) {
        return (
            <div style={{ maxWidth: '400px', margin: '100px auto', padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
                <h2>Zaloguj się</h2>
                <p>Aplikacja zabezpieczona przez OAuth 2.0 PKCE</p>
                <button onClick={() => void auth.signinRedirect()} style={{ padding: '10px', background: '#007bff', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%' }}>
                    Zaloguj przez Zitadel
                </button>
            </div>
        );
    }

    // GŁÓWNY EKRAN PO ZALOGOWANIU

    console.log("Twój profil OIDC:", auth.user?.profile);

    let displayName = 'Użytkowniku';
    if (auth.user?.profile) {
        displayName = auth.user.profile.preferred_username
            || auth.user.profile.name
            || auth.user.profile.email
            || 'Użytkowniku';
    }
    const testAdminRights = async () => {
        const res = await fetch('/api/tasks/all', { headers: getHeaders() });
        if (res.status === 403) {
            alert("Odmowa dostępu (403): Nie masz roli administratora!");
        } else if (res.ok) {
            const data = await res.json();
            alert(`Sukces (Zalogowano jako admin). W całym systemie jest ${data.length} zadań.`);
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#fff', padding: '10px 20px', borderRadius: '8px', marginBottom: '20px' }}>
                <h2>Witaj, {displayName}!</h2>
                {/*<div>*/}
                {/*    <button onClick={testAdminRights} style={{ padding: '5px 10px', background: '#ffc107', color: '#000', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '10px' }}>*/}
                {/*        Testuj Rolę (Admin)*/}
                {/*    </button>*/}
                {/*    <button onClick={() => void auth.signoutRedirect()} style={{ padding: '5px 10px', background: '#dc3545', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Wyloguj</button>*/}
                {/*</div>*/}
                <div>
                    <button onClick={() => void auth.signoutRedirect()} style={{ padding: '5px 10px', background: '#dc3545', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Wyloguj</button>
                </div>
            </div>

            {/* SEKCJA ZADAŃ */}
            <div style={{ background: '#fff', padding: '20px', borderRadius: '8px', marginBottom: '20px', borderLeft: '5px solid #17a2b8' }}>
                <h3>Zadania (Chronione)</h3>
                <form onSubmit={addTask} style={{ display: 'flex', gap: '10px', marginBottom: '15px' }}>
                    <input type="text" placeholder="Tytuł zadania" value={taskTitle} onChange={e => setTaskTitle(e.target.value)} required style={{ padding: '8px', flex: 1 }} />
                    <button type="submit" style={{ padding: '8px 15px', background: '#17a2b8', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Dodaj</button>
                </form>
                {tasks.length === 0 ? <p>Brak zadań.</p> : (
                    <ul style={{ listStyle: 'none', padding: 0 }}>
                        {tasks.map(t => (
                            <li key={t.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', borderBottom: '1px solid #eee' }}>
                                <div><strong>{t.title}</strong></div>
                                <button onClick={() => deleteTask(t.id)} style={{ background: '#dc3545', color: '#fff', border: 'none', padding: '3px 8px', borderRadius: '4px', cursor: 'pointer' }}>Usuń</button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            {/* SEKCJA SPOTKAŃ */}
            <div style={{ background: '#fff', padding: '20px', borderRadius: '8px', borderLeft: '5px solid #28a745' }}>
                <h3>Spotkania</h3>
                <form onSubmit={addMeeting} style={{ display: 'flex', gap: '10px', marginBottom: '15px' }}>
                    <input type="text" placeholder="Tytuł spotkania" value={meetingTitle} onChange={e => setMeetingTitle(e.target.value)} required style={{ padding: '8px', flex: 1 }} />
                    <input type="text" placeholder="Opis" value={meetingDesc} onChange={e => setMeetingDesc(e.target.value)} required style={{ padding: '8px', flex: 2 }} />
                    <button type="submit" style={{ padding: '8px 15px', background: '#28a745', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Dodaj</button>
                </form>
                {meetings.length === 0 ? <p>Brak spotkań.</p> : (
                    <ul style={{ listStyle: 'none', padding: 0 }}>
                        {meetings.map(m => (
                            <li key={m.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', borderBottom: '1px solid #eee' }}>
                                <div><strong>{m.title}</strong> - {m.description}</div>
                                <button onClick={() => deleteMeeting(m.id)} style={{ background: '#dc3545', color: '#fff', border: 'none', padding: '3px 8px', borderRadius: '4px', cursor: 'pointer' }}>Usuń</button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}