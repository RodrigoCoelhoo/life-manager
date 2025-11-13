import { Outlet } from "react-router-dom";
import HomeHeader from "../components/common/HomeHeader.tsx"
import Footer from "../components/common/Footer.tsx";

const HomeLayout = () => {
	return (
		<>
			<HomeHeader />
			<Outlet />
			<Footer />
		</>
	);
};
export default HomeLayout;