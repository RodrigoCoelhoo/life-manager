import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import logo from "../../assets/logo64.png"
import { FaUserCircle } from 'react-icons/fa';

export default function Sidebar() {
	const { username } = useAuth();

	return (
		<aside className="h-screen flex flex-col justify-between w-60 shrink-0 bg-background drop-shadow-[0_4px_6px_rgba(0,0,0,0.2)] text-textcolor">

			<div className="flex flex-col flex-1 overflow-hidden">
				<div className="w-full border-b-4 border-background-accent p-2">
					<img src={logo} alt="LifeManager Logo" />
				</div>

				<div className="flex-1 overflow-y-auto p-4 space-y-6 scrollbar-hide">

					<div>
						<div className="flex flex-row items-center border-b-4 mb-1 border-background-accent">
							<h2 className="menu-title">Finances</h2>
						</div>
						<nav className="flex flex-col gap-2">
							<NavLink
								to="/dashboard"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Dashboard
							</NavLink>

							<NavLink
								to="/wallets"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Wallets
							</NavLink>

							<NavLink
								to="/transactions"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Transactions
							</NavLink>

							<NavLink
								to="/transferences"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Transferences
							</NavLink>
						</nav>
					</div>

					<div>
						<div className="flex flex-row items-center border-b-4 mb-1 border-background-accent">
							<h2 className="menu-title">Nutrition</h2>
						</div>
						<nav className="flex flex-col gap-2">
							<NavLink
								to="/ingredients"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Ingredients
							</NavLink>
							<NavLink
								to="/recipes"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Recipes
							</NavLink>
							<NavLink
								to="/meals"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Meals
							</NavLink>
						</nav>
					</div>

					<div>
						<div className="flex flex-row items-center border-b-4 mb-1 border-background-accent">
							<h2 className="menu-title">Training</h2>
						</div>
						<nav className="flex flex-col gap-2">
							<NavLink
								to="/exercises"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Exercises
							</NavLink>
							<NavLink
								to="/training-plans"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Training Plans
							</NavLink>
							<NavLink
								to="/training-sessions"
								className={({ isActive }) =>
									`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal" : ""}`
								}
							>
								Training Sessions
							</NavLink>
						</nav>
					</div>
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
