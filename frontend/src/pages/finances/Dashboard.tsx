import { useAuth } from "../../contexts/AuthContext";

export default function Dashboard() {
	const { username } = useAuth();

	return (
		<>
			<h1 className="mt-20">DASHBOARD</h1>
			<p>LOGGED IN</p>
			<p className="px-10">{username}</p>
		</>
	);
}