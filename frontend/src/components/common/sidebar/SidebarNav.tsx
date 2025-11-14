import { NavLink } from "react-router-dom";
import { useState } from "react";

import { GrMoney } from "react-icons/gr";
import { GiWeightLiftingUp, GiHotMeal } from "react-icons/gi";
import { IoIosArrowForward } from "react-icons/io";

export default function SidebarNav({ onNavigate }: { onNavigate?: () => void }) {

	const [isFinancesActive, setIsFinancesActive] = useState(true);
	const [isNutritionActive, setIsNutritionActive] = useState(true);
	const [isTrainingActive, setIsTrainingActive] = useState(true);

	const toggleFinances = () => setIsFinancesActive(!isFinancesActive);
	const toggleNutrition = () => setIsNutritionActive(!isNutritionActive);
	const toggleTraining = () => setIsTrainingActive(!isTrainingActive);

	const closeOnMobile = () => {
		if (onNavigate) onNavigate();
	};

	return (
		<div className="flex-1 overflow-y-auto p-4 space-y-6 scrollbar-hide">

			<div>
				<button
					className={`w-full flex flex-row items-center mb-1 cursor-pointer ${isFinancesActive ? "border-b-4 border-background-accent" : "border-b-0 border-transparent"} hover:bg-background-accent hover:rounded-xl`}
					onClick={toggleFinances}
				>
					<h2 className="menu-title w-full flex justify-between items-center">
						<span className="flex items-center gap-4">
							<GrMoney size={16} /> Finances
						</span>
						<IoIosArrowForward
							size={16}
							className={`mr-4 ${isFinancesActive ? "rotate-90" : ""} transition-transform`}
						/>
					</h2>
				</button>

				<nav className={`flex-col gap-2 overflow-hidden ${isFinancesActive ? "max-h-96 opacity-100" : "max-h-0 opacity-0"} transition-all`}>
					<MobileOption to="/dashboard" onClick={closeOnMobile} />
					<MobileOption to="/wallets" onClick={closeOnMobile} />
					<MobileOption to="/transactions" onClick={closeOnMobile} />
					<MobileOption to="/transferences" onClick={closeOnMobile} />
				</nav>
			</div>

			<div>
				<button
					className={`w-full flex items-center mb-1 cursor-pointer ${isNutritionActive ? "border-b-4 border-background-accent" : "border-b-0"} hover:bg-background-accent hover:rounded-xl`}
					onClick={toggleNutrition}
				>
					<h2 className="menu-title w-full flex justify-between items-center">
						<span className="flex items-center gap-4">
							<GiHotMeal size={16} /> Nutrition
						</span>
						<IoIosArrowForward
							size={16}
							className={`mr-4 ${isNutritionActive ? "rotate-90" : ""} transition-transform`}
						/>
					</h2>
				</button>

				<nav className={`flex-col gap-2 overflow-hidden ${isNutritionActive ? "max-h-96 opacity-100" : "max-h-0 opacity-0"} transition-all`}>
					<MobileOption to="/ingredients" onClick={closeOnMobile} />
					<MobileOption to="/recipes" onClick={closeOnMobile} />
					<MobileOption to="/meals" onClick={closeOnMobile} />
				</nav>
			</div>

			<div>
				<button
					className={`w-full flex items-center mb-1 cursor-pointer ${isTrainingActive ? "border-b-4 border-background-accent" : "border-b-0"} hover:bg-background-accent hover:rounded-xl`}
					onClick={toggleTraining}
				>
					<h2 className="menu-title w-full flex justify-between items-center">
						<span className="flex items-center gap-4">
							<GiWeightLiftingUp size={16} /> Training
						</span>
						<IoIosArrowForward
							size={16}
							className={`mr-4 ${isTrainingActive ? "rotate-90" : ""} transition-transform`}
						/>
					</h2>
				</button>

				<nav className={`flex-col gap-2 overflow-hidden ${isTrainingActive ? "max-h-96 opacity-100" : "max-h-0 opacity-0"} transition-all`}>
					<MobileOption to="/exercises" onClick={closeOnMobile} />
					<MobileOption to="/training-plans" onClick={closeOnMobile} />
					<MobileOption to="/training-sessions" onClick={closeOnMobile} />
				</nav>
			</div>

		</div>
	);
}


function MobileOption({
	to,
	onClick,
}: { to: string; onClick: () => void }) {

	const label = to.replace("/", "").replace("-", " ");

	return (
		<NavLink
			to={to}
			onClick={onClick}
			className={({ isActive }) =>
				`menu-option ${isActive ? "bg-foreground border-l-5 border-primary font-normal text-secondary" : ""}`
			}
		>
			{label.charAt(0).toUpperCase() + label.slice(1)}
		</NavLink>
	);
}
