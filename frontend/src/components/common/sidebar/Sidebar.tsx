import { Link } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContext";
import logo from "../../../assets/logo.png"
import { FaUserCircle } from 'react-icons/fa';
import SidebarNav from "./SidebarNav";

export default function Sidebar() {
	const { username } = useAuth();

	return (
		<aside className="h-screen flex flex-col justify-between w-60 shrink-0 bg-background drop-shadow-[0_4px_6px_rgba(0,0,0,0.2)] text-textcolor scrollbar-hide">
			<div className="flex flex-col flex-1 overflow-hidden">
				<div className="border-b-4 border-background-accent p-2 flex items-center justify-center">
					<Link
						to="/home">
						<img src={logo} alt="LifeManager Logo" className="h-16"/>
					</Link>

				</div>

				<div className="flex-1 overflow-y-auto">
					<SidebarNav />
				</div>
			</div>

			<div className="w-full h-30 p-5 border-t-4 border-background-accent bg-background shrink-0">
				<Link
					to="/profile"
					className="cursor-pointer w-full h-full flex flex-row gap-4 justify-center items-center hover:bg-background-accent rounded-2xl"
				>
					<FaUserCircle size={32} />
					<div>
						<h2 className="font-semibold">{username}</h2>
						<h3 className="text-sm font-extralight">Account Settigns</h3>
					</div>
				</Link>
			</div>
		</aside>
	);
}
