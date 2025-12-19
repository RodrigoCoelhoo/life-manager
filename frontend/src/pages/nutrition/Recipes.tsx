export default function Recipes() {
	return (
		<>
			<span className="text-textcolor">Selecionar ingredientes que tenho, devolve lista com as receitas que têm todos os ingredientes selecionados</span>
			<br />
			<span className="text-textcolor">Exemplo seleciono pão, manteiga, fiambre - Posso fazer uma torrada porque uma torrada só precisa de pão e manteiga, mas não posso fazer uma tosta porque falta queijo</span>

			<div className="h-full w-full p-6 text-textcolor text-4xl rounded-lg grid gap-2 grid-rows-[1fr_3fr_1fr] grid-cols-3">

				<div className="bg-foreground flex justify-center items-center rounded-lg col-span-1">
					Container 1
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg col-span-1">
					Container 2
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg row-span-2 col-span-1">
					Container 3
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg col-span-2">
					Recipes
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg col-span-1">
					Container 4
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg col-span-2">
					Container 5
				</div>
			</div>
		</>
	);
}
