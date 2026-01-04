import { Link } from 'react-router-dom';
import reactLogo from '../assets/react.svg';
import springLogo from '../assets/spring.svg';
import tailwindLogo from '../assets/tailwind.svg';
import dashboard from '../assets/homepageimage.png';
import meal from '../assets/meal.png';
import finances from '../assets/finances.png';
import exercise from '../assets/exercise.png';
import { useAuth } from '../contexts/AuthContext';
import { useInView } from '../hooks/useInView';

interface Feature {
	title: string;
	description: string;
	image: string;
	reverse?: boolean;
}

interface Tech {
	title: string;
	description: string;
	logo: string;
	link: string;
	animation: string;
}

export default function HomePage() {
	const { isLoggedIn } = useAuth();

	const [heroRef, isHeroVisible] = useInView<HTMLDivElement>({ threshold: 0.2 });

	const features: Feature[] = [
		{
			title: 'Finances',
			description:
				'Stay in control of your finances with custom wallets, categorized transactions, and easy transfers. Get real-time insights into your monthly spending through intuitive, interactive dashboards.',
			image: finances,
		},
		{
			title: 'Nutrition',
			description:
				'Keep track of your daily meals by creating ingredients and recipes with full nutritional data. Build meals from your saved ingredients or recipes and monitor your calorie and macro intake with ease.',
			image: meal,
			reverse: true,
		},
		{
			title: 'Training',
			description:
				'Build structured training plans and log your sessions with precision. Create cardio or strength exercises, track sets, reps, weights, and duration, and monitor your progress as you advance toward your fitness goals.',
			image: exercise,
		},
	];

	const technologies: Tech[] = [
		{
			title: 'Spring Boot',
			description: 'Java-based backend framework for building scalable REST APIs.',
			logo: springLogo,
			link: 'https://spring.io/projects/spring-boot',
			animation: 'animate-float',
		},
		{
			title: 'React',
			description: 'JavaScript library for building user interfaces. (TypeScript)',
			logo: reactLogo,
			link: 'https://react.dev',
			animation: 'animate-spin-slow',
		},
		{
			title: 'Tailwind',
			description: 'Utility-first CSS framework for building responsive, modern UIs.',
			logo: tailwindLogo,
			link: 'https://tailwindcss.com/',
			animation: 'animate-wave',
		},
	];

	return (
		<div className="bg-background font-sans">
			<main>
				{/* Hero Section */}
				<section
					ref={heroRef}
					className={`bg-background text-textcolor flex flex-col items-center justify-between xl:flex-row overflow-hidden pb-32 transition-all duration-700 ${isHeroVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
						}`}
				>
					<div className="bg-background flex flex-col items-center xl:items-start text-center xl:text-left w-3/5 mt-25 mb-25 px-8 sm:px-16 md:px-24 lg:px-32 xl:px-48">
						<h1 className="text-5xl sm:text-6xl font-bold mb-6 leading-tight">
							Visualize & Organize<br />
							<span className="text-primary">Your Life</span>
						</h1>
						<p className="text-xl max-w-2xl text-textcolor/80 mb-10 pr-10">
							Track your <span className="text-secondary font-semibold">finances</span>, plan your{' '}
							<span className="text-secondary font-semibold">meals</span>, and monitor your{' '}
							<span className="text-secondary font-semibold">trainings</span>.
						</p>
						<div className="flex flex-row gap-4">
							<Link
								to={isLoggedIn ? '/finances-dashboard' : '/signup'}
								className="bg-primary hover:bg-primary/80 text-white font-semibold px-8 py-3 rounded-lg shadow-lg transition-all hover:cursor-pointer"
							>
								Get Started
							</Link>
							<button
								onClick={() =>
									window.open('https://github.com/RodrigoCoelhoo/life-manager', '_blank')
								}
								className="bg-foreground border border-secondary text-textcolor font-semibold px-8 py-3 rounded-lg hover:border-primary hover:text-primary transition-all hover:cursor-pointer"
							>
								Github
							</button>
						</div>
					</div>
					<div className="w-2/5">
						<img src={dashboard} alt="Dashboard" className="hidden xl:block" />
					</div>
				</section>

				<section className="flex flex-col text-textcolor gap-20 mt-20">
					<h2 className="text-5xl font-bold text-textcolor text-center drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]">
						Features
					</h2>

					{features.map((feature, idx) => {
						const [ref, isVisible] = useInView<HTMLDivElement>({ threshold: 0.3 });

						const fromRight = idx % 2 === 0;

						return (
							<div
								key={idx}
								ref={ref}
								className={`bg-foreground 
									mx-4 lg:mx-20 
									px-6 lg:px-16 py-10 
									rounded-4xl drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]
									transition-all duration-700
									${isVisible
										? 'opacity-100 translate-x-0'
										: fromRight
											? 'opacity-0 translate-x-20'
											: 'opacity-0 -translate-x-20'
									}`}
							>
								<div className={`flex flex-col lg:flex-row ${feature.reverse ? 'lg:flex-row-reverse' : ''} items-center justify-between gap-10`}>
									<div className={`lg:w-3/5 text-center ${feature.reverse ? 'lg:text-right' : 'lg:text-left'}`}>
										<h3 className="text-3xl font-semibold mb-2">{feature.title}</h3>
										<p className="text-textcolor/80 font-extralight leading-relaxed text-lg">{feature.description}</p>
									</div>
									<img src={feature.image} alt={`${feature.title} dashboard`} className="h-52 sm:h-60 md:h-72 xl:h-96 object-contain lg:w-2/5 transition-all duration-300" />
								</div>
							</div>
						);
					})}

				</section>

				<section className="bg-background flex flex-col items-center px-2 sm:px-4 md:px-8 lg:px-16 xl:px-32 py-32">
					<h2 className="text-5xl font-bold text-textcolor mt-10 mb-20">Technologies Used</h2>

					<div className="flex flex-col sm:flex-row flex-wrap justify-center items-center w-full gap-8 h-auto">
						{technologies.map((tech, idx) => {
							const [ref, isVisible] = useInView<HTMLAnchorElement>({ threshold: 0.2 });
							return (
								<a
									key={idx}
									ref={ref}
									href={tech.link}
									target="_blank"
									rel="noopener noreferrer"
									className={`group bg-foreground w-96 h-80 p-4 rounded-lg border-2 border-secondary shadow-[0_0_15px_var(--color-border)] transition-all duration-700 transform ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
										} hover:border-primary hover:shadow-[0_0_25px_var(--color-secondary)] hover:scale-[1.02] block`}
								>
									<img
										src={tech.logo}
										alt={`${tech.title} Logo`}
										className={`${tech.animation} w-16 h-16 mx-auto my-4`}
									/>
									<h3 className="text-textcolor flex justify-center font-bold text-3xl mx-16 pb-1 border-b-8 border-secondary transition-colors duration-300 group-hover:border-primary gap-2">
										{tech.title}
									</h3>
									<p className="text-textcolor flex justify-center text-center text-xl my-5 mx-12">
										{tech.description}
									</p>
								</a>
							);
						})}
					</div>
				</section>
			</main>
		</div>
	);
}
