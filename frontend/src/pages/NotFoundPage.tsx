import { Link } from "react-router-dom";

const NotFoundPage = () => {
	return (
		<section className="bg-background flex flex-col text-textcolor text-center justify-center items-center min-h-[calc(100vh-8rem)]">
			<h1 className="text-6xl font-bold mb-4">404 Not Found</h1>
			<p className="font-extralight text-xl mb-5">This page does not exist.</p>
			<Link
				to="/home"
				className="rounded-xl px-3 py-2 mt-4 border-2 border-secondary/80 hover:border-primary/80 text-white transition-all duration-300 shadow-none hover:shadow-[0_0_12px] hover:shadow-secondary hover:text-secondary"
			>
				Go Back
			</Link>
		</section>
	);
};
export default NotFoundPage