import { Outlet } from "react-router-dom";
import HomeHeader from "../components/common/header/HomeHeader.tsx"
import MobileHeader from "../components/common/header/MobileHeader.tsx"
import Footer from "../components/common/Footer.tsx";
import { useAuth } from "../contexts/AuthContext";

const HomeLayout = () => {
	const { isLoggedIn } = useAuth();

	return (
		<>
			{isLoggedIn ? (
				<MobileHeader />
			) : (
				<HomeHeader />
			)}
			<Outlet />
			<Footer />
		</>
	);
};
export default HomeLayout;