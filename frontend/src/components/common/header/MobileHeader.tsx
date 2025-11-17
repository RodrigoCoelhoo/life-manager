import { useState } from "react";
import { FaUserCircle } from "react-icons/fa";
import { RiMenu2Line, RiMenuLine, RiMenu4Line } from "react-icons/ri";
import SidebarNav from "../sidebar/SidebarNav";
import logo from "../../../assets/logo64.png"
import { Link } from "react-router-dom";

export default function MobileHeader() {
	const [open, setOpen] = useState(false);

	return (
		<>
			<header className="relative w-full h-16 bg-background shadow-md flex items-center justify-between px-4 text-textcolor z-50">

				<button className="relative flex items-center pl-4 hover:text-secondary">
					<div className="relative group cursor-pointer" onClick={() => setOpen(!open)}>
						{open ? (
							<RiMenu4Line
								size={28}
								className="transition-all duration-300"
							/>
						) : (
							<>
								<RiMenuLine
									size={28}
									className="transition-all duration-300 group-hover:opacity-0 group-hover:scale-90"
								/>

								<RiMenu2Line
									size={28}
									className="absolute inset-0 transition-all duration-300 opacity-0 scale-90 group-hover:opacity-100 group-hover:scale-100"
								/>
							</>
						)}
					</div>
				</button>

				<Link
					to="/home"
					className="absolute left-1/2 transform -translate-x-1/2">
					<img src={logo} alt="LifeManager Logo" className="h-16" />
				</Link>

				<Link
					to="/profile"
					className="p-1 mr-2 hover:bg-secondary/20 rounded-4xl"
					onClick={() => setOpen(false)}>
					<FaUserCircle size={32} />
				</Link>
			</header >


			{open && (
				<div className="fixed inset-x-0 top-16 bottom-0 bg-black/60 z-40">
					<div className="absolute inset-0 bg-background flex flex-col flex-1 overflow-y-auto">
						<SidebarNav onNavigate={() => setOpen(false)} />
					</div>
				</div>
			)
			}
		</>
	);
}
