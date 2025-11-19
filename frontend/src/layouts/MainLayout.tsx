import { Outlet } from "react-router-dom";
import Sidebar from '../components/common/sidebar/Sidebar';
import MobileHeader from "../components/common/header/MobileHeader";

const MainLayout = () => {
	return (
		<div className="flex flex-col h-screen bg-background scrollbar-custom ">

			<div className="lg:hidden">
				<MobileHeader />
			</div>

			<div className="flex flex-1 overflow-hidden flex-col lg:flex-row">
				<div className="hidden lg:block">
					<Sidebar />
				</div>

				<main className="flex-1 min-h-0 overflow-y-auto bg-background">
					<Outlet />
				</main>

			</div>
		</div>
	);
};

export default MainLayout;
