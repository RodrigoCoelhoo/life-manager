import { useAuth } from "../contexts/AuthContext"

export default function Profile() {
	const { logout } = useAuth();

	return (
		<div className="h-full w-full p-6 text-textcolor text-4xl rounded-lg">
			<div className="bg-foreground flex flex-col justify-center items-center rounded-lg col-span-1 h-full gap-10">
				<h1 className='menu-title text-5xl'>Profile</h1>
				<button
					onClick={logout}
					className='bg-background py-3 px-8 rounded-2xl border-2 border-secondary'
				>
					Logout
				</button>
			</div>
		</div>
	);
}
