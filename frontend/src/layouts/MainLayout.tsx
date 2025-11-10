import { Outlet } from "react-router-dom";
import Header from "../components/HomePage/Header.tsx"
import Footer from "../components/HomePage/Footer.tsx";

const MainLayout = () => {
	return (
		<>
			<Header />
			<Outlet />
			<Footer />
		</>
	);
};
export default MainLayout;