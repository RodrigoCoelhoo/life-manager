import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { jwtDecode } from "jwt-decode";
import { registerLogout, setAccessToken as setApiToken } from "../services/api";
import toast from "react-hot-toast";

interface TokenPayload {
	sub: string; // username
	exp: number;
	iat: number;
}

interface AuthContextType {
	isLoggedIn: boolean;
	username: string | null;
	setAccessToken: (token: string) => void;
	logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
	const [username, setUsername] = useState<string | null>(null);

	useEffect(() => {
		const token = localStorage.getItem('accessToken');
		if (token) {
			try {
				const decoded = jwtDecode<TokenPayload>(token);
				setUsername(decoded.sub);
				setApiToken(token);
			} catch {
				setUsername("Error");
			}
		}
	}, []);

	const handleSetAccessToken = (token: string) => {
		if (token) {
			localStorage.setItem('accessToken', token);
			setApiToken(token);
			try {
				const decoded = jwtDecode<TokenPayload>(token);
				setUsername(decoded.sub);
			} catch {
				setUsername("Error");
			}
		} else {
			localStorage.removeItem('accessToken');
			setApiToken('');
			setUsername(null);
		}
	};

	const logout = () => {
		handleSetAccessToken('');
		toast.error("Session Expired! Please sign in again.");
		setTimeout(() => {
			window.location.href = "/login";
		}, 1500);
	};

	useEffect(() => {
		registerLogout(logout);
	}, [logout]);

	return (
		<AuthContext.Provider value={{
			isLoggedIn: !!localStorage.getItem("accessToken"),
			username,
			setAccessToken: handleSetAccessToken,
			logout,
		}}>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = () => {
	const context = useContext(AuthContext);
	if (!context) throw new Error("useAuth must be used within an AuthProvider");
	return context;
};