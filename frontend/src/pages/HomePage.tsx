import { Link } from 'react-router-dom'
import reactLogo from '../assets/react.svg'
import springLogo from '../assets/spring.svg'
import tailwindLogo from '../assets/tailwind.svg'
import dashboard from '../assets/dashboard.png'
import meal from '../assets/meal.png'
import finances from '../assets/finances.png'
import exercise from '../assets/exercise.png'
import { useAuth } from '../contexts/AuthContext'

export default function HomePage() {
	const { isLoggedIn } = useAuth();

	return (
		<div className="bg-background font-sans">
			<main className="pt-16">
				<section className="bg-background text-textcolor flex flex-col xl:flex-row overflow-hidden py-32">

					<div className="bg-background flex flex-col items-center xl:items-start text-center xl:text-left w-full mt-25 mb-25 px-8 sm:px-16 md:px-24 lg:px-32 xl:px-48">
						<h1 className="text-5xl sm:text-6xl font-bold mb-6 leading-tight">
							Visualize & Organize<br />
							<span className="text-primary">Your Life</span>
						</h1>

						<p className="text-xl max-w-2xl text-textcolor/80 mb-10 pr-10">
							Track your <span className="text-secondary font-semibold">finances</span>,
							plan your <span className="text-secondary font-semibold">meals</span>,
							and monitor your <span className="text-secondary font-semibold">trainings</span>.
						</p>

						<div className="flex flex-row gap-4">
							<Link
								to={isLoggedIn ? "/finances-dashboard" : "/signup"}
								className="bg-primary hover:bg-primary/80 text-white font-semibold px-8 py-3 rounded-lg shadow-lg transition-all hover:cursor-pointer"
							>
								Get Started
							</Link>
							<button
								onClick={() => window.open("https://github.com/RodrigoCoelhoo/life-manager", "_blank")}
								className="bg-foreground border border-secondary text-textcolor font-semibold px-8 py-3 rounded-lg hover:border-primary hover:text-primary transition-all hover:cursor-pointer"
							>
								Github
							</button>
						</div>
					</div>

					<img
						src={dashboard}
						alt="Dashboard"
						className="h-170 xl:ml-10  hidden xl:block"
					/>

				</section>

				<section className="flex flex-col text-textcolor gap-20">
					<h2 className="text-5xl font-bold text-textcolor text-center mt-10 mb-20 drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]">
						Features
					</h2>

					<div className="bg-foreground mx-4 lg:mx-20 px-6 lg:px-16 py-10 rounded-4xl drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]">
						<div className="flex flex-col lg:flex-row items-center justify-between gap-10">
							<div className="lg:w-3/5 text-center lg:text-left">
								<h3 className="text-3xl font-semibold mb-2">Finances</h3>
								<p className="text-textcolor/80 font-extralight leading-relaxed text-lg">
									Stay in control of your finances with custom wallets, categorized transactions, and easy transfers.
									Get real-time insights into your monthly spending through intuitive, interactive dashboards.
								</p>
							</div>
							<img
								src={finances}
								alt="Finance dashboard"
								className="h-52 sm:h-60 md:h-72 xl:h-96 object-contain lg:w-2/5 transition-all duration-300"
							/>
						</div>
					</div>

					<div className="bg-foreground mx-4 lg:mx-20 px-6 lg:px-16 py-10 rounded-4xl drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]">
						<div className="flex flex-col lg:flex-row-reverse items-center justify-between gap-10">
							<div className="lg:w-3/5 text-center lg:text-right">
								<h3 className="text-3xl font-semibold mb-2">Nutrition</h3>
								<p className="text-textcolor/80 font-extralight leading-relaxed text-lg">
									Keep track of your daily meals by creating ingredients and recipes with full nutritional data.
									Build meals from your saved ingredients or recipes and monitor your calorie and macro intake with ease.
								</p>
							</div>
							<img
								src={meal}
								alt="Meal tracking dashboard"
								className="h-52 sm:h-60 md:h-72 xl:h-96 object-contain lg:w-2/5 transition-all duration-300"
							/>
						</div>
					</div>

					<div className="bg-foreground mx-4 lg:mx-20 px-6 lg:px-16 py-10 rounded-4xl drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]">
						<div className="flex flex-col lg:flex-row items-center justify-between gap-10">
							<div className="lg:w-3/5 text-center lg:text-left">
								<h3 className="text-3xl font-semibold mb-2">Training</h3>
								<p className="text-textcolor/80 font-extralight leading-relaxed text-lg">
									Build structured training plans and log your sessions with precision.
									Create cardio or strength exercises, track sets, reps, weights, and duration, and monitor your progress as you advance toward your fitness goals.
								</p>
							</div>
							<img
								src={exercise}
								alt="Workout dashboard"
								className="h-60 sm:h-64 md:h-72 xl:h-96 object-contain lg:w-2/5 transition-all duration-300"
							/>
						</div>
					</div>
				</section>


				<section className="bg-background flex flex-col items-center px-2 sm:px-4 md:px-8 lg:px-16 xl:px-32 py-32">
					<h2 className="text-5xl font-bold text-textcolor mt-10 mb-20">Technologies Used</h2>

					<div className="flex flex-col sm:flex-row flex-wrap justify-center items-center w-full gap-8 h-auto">

						<a
							href="https://spring.io/projects/spring-boot"
							target="_blank"
							rel="noopener noreferrer"
							className="group bg-foreground w-96 h-80 p-4 rounded-lg border-2 border-secondary 
              shadow-[0_0_15px_var(--color-border)] transition-all duration-300 
              hover:border-primary hover:shadow-[0_0_25px_var(--color-secondary)] hover:scale-[1.02] block"
						>
							<img
								src={springLogo}
								alt="Spring Logo"
								className="animate-float w-16 h-16 mx-auto my-4"
							/>
							<h3
								className="text-textcolor flex justify-center font-bold text-3xl mx-16 pb-1 
                border-b-8 border-secondary transition-colors duration-300 group-hover:border-primary"
							>
								Spring Boot
							</h3>
							<p className="text-textcolor flex justify-center text-center text-xl my-5 mx-12">
								Java-based backend framework for building scalable REST APIs.
							</p>
						</a>

						<a
							href="https://react.dev"
							target="_blank"
							rel="noopener noreferrer"
							className="group bg-foreground w-96 h-80 p-4 rounded-lg border-2 border-secondary 
              shadow-[0_0_15px_var(--color-border)] transition-all duration-300 
              hover:border-primary hover:shadow-[0_0_25px_var(--color-secondary)] hover:scale-[1.02] block"
						>
							<img
								src={reactLogo}
								alt="React Logo"
								className="animate-spin-slow w-16 h-16 mx-auto my-4"
							/>
							<h3
								className="text-textcolor flex justify-center font-bold text-3xl mx-16 pb-1 
                border-b-8 border-secondary transition-colors duration-300 group-hover:border-primary gap-2"
							>
								React
							</h3>
							<p className="text-textcolor flex justify-center text-center text-xl my-5 mx-12">
								JavaScript library for building user interfaces. (TypeScript)
							</p>
						</a>

						<a
							href="https://tailwindcss.com/"
							target="_blank"
							rel="noopener noreferrer"
							className="group bg-foreground w-96 h-80 p-4 rounded-lg border-2 border-secondary 
              shadow-[0_0_15px_var(--color-border)] transition-all duration-300 
              hover:border-primary hover:shadow-[0_0_25px_var(--color-secondary)] hover:scale-[1.02] block"
						>
							<img
								src={tailwindLogo}
								alt="Tailwind Logo"
								className="animate-wave w-16 h-16 mx-auto my-4"
							/>
							<h3
								className="text-textcolor flex justify-center font-bold text-3xl mx-16 pb-1 
                border-b-8 border-secondary transition-colors duration-300 group-hover:border-primary"
							>
								Tailwind
							</h3>
							<p className="text-textcolor flex justify-center text-center text-xl my-5 mx-12">
								Utility-first CSS framework for building responsive, modern UIs.
							</p>
						</a>

					</div>
				</section>
			</main>
		</div>
	);
}
