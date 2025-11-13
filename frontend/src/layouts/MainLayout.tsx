import { Outlet } from "react-router-dom";
import Sidebar from '../components/common/Sidebar';

const MainLayout = () => {
	return (
		<div className="flex flex-col h-screen">
			<div className="flex flex-1 overflow-hidden">
				<Sidebar />

				<main className="flex-1 overflow-y-auto bg-background">
					<Outlet />
				</main>
			</div>
		</div>
	);
};
export default MainLayout;