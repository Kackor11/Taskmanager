import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { AuthProvider } from "react-oidc-context";

const oidcConfig = {
    authority: "http://localhost:8081",
    client_id: "377891374571257861",
    redirect_uri: "http://localhost/",
    post_logout_redirect_uri: "http://localhost/",
    response_type: "code", // wymusza standard PKCE
    scope: "openid profile email urn:zitadel:iam:org:project:roles"
};

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <AuthProvider {...oidcConfig}>
            <App />
        </AuthProvider>
    </React.StrictMode>,
)