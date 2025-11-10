import { createContext, useContext, useState, type ReactNode, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

interface TokenPayload {
	sub: string; // username
	exp: number;
	iat: number;
}

interface AuthContextType {
	isLoggedIn: boolean;
	username: string | null;
	login: (token: string) => void;
	logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
	const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));
	const [username, setUsername] = useState<string | null>(null);

	useEffect(() => {
		if (token) {
			try {
				const decoded = jwtDecode<TokenPayload>(token);
				setUsername(decoded.sub);
			} catch {
				setUsername(null);
			}
		} else {
			setUsername(null);
		}
	}, [token]);

	const login = (newToken: string) => {
		localStorage.setItem("token", newToken);
		setToken(newToken);
	};

	const logout = () => {
		localStorage.removeItem("token");
		setToken(null);
	};

	return (
		<AuthContext.Provider value={{ isLoggedIn: !!token, username, login, logout }}>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = () => {
	const context = useContext(AuthContext);
	if (!context) throw new Error("useAuth must be used within an AuthProvider");
	return context;
};