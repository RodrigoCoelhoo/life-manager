import { useState } from "react";
import { Modal } from "../common/Modal";
import { FaCircle } from "react-icons/fa";
import type { RecipeDetailsDTO, RecipeDTO } from "../../services/nutrition/recipe/recipe.dto";
import RecipeForm from "./RecipeForm";

interface RecipeCardProps extends RecipeDetailsDTO {
	onUpdate: (id: number, data: RecipeDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function TrainingPlanCard({ onUpdate, onDelete, ...recipe }: RecipeCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<div
				className="p-4 bg-foreground rounded-lg flex flex-col justify-between gap-2 shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary"
				onClick={() => setIsOpen(true)}
			>
				<div>
					<h2 className="text-xl font-bold mb-2">{recipe.name}</h2>

					<ul className="max-h-60 overflow-y-auto flex flex-col list-disc list-inside font-extralight">
						{recipe.ingredients.map((ingredient) => (
							<li
								key={ingredient.ingredient.id}
								className="flex justify-between"
							>
								<div className="flex flex-row gap-2 items-center">
									<FaCircle size={6} className="text-secondary mt-1" />
									<span className="">{ingredient.ingredient.name}</span>
								</div>
								<div className="text-xs text-gray-400">{ingredient.amount.toFixed(2)} {ingredient.unit}</div>
							</li>
						))}
					</ul>
				</div>
			</div>


			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<RecipeForm
					recipe={recipe}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
