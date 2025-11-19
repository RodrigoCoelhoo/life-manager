import logo from "../../assets/logo.png";

export default function Loading() {
	return (
		<div className="flex flex-col justify-center items-center min-h-screen">
			<img src={logo} alt="LifeManager Logo" className="h-96 animate-float" />
			<div className="flex justify-center items-center gap-6">
				<span className="w-12 h-12 bg-secondary rounded-full animate-bounce" style={{ animationDelay: '0s' }}></span>
				<span className="w-12 h-12 bg-secondary rounded-full animate-bounce" style={{ animationDelay: '0.15s' }}></span>
				<span className="w-12 h-12 bg-secondary rounded-full animate-bounce" style={{ animationDelay: '0.3s' }}></span>
			</div>
		</div>
	);
}
