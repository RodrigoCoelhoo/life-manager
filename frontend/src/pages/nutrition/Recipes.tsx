import { useEffect, useState } from "react";
import type { PageResponseDTO } from "../../services/api.dto";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Modal } from "../../components/common/Modal";
import { Pagination } from "../../components/common/Pagination";
import toast from "react-hot-toast";
import { IoMdOptions } from "react-icons/io";
import { SearchBar } from "../../components/common/SearchBar";
import { Unit, type RecipeDetailsDTO, type RecipeDTO, type RecipeIngredientResponseDTO } from "../../services/nutrition/recipe/recipe.dto";
import { recipeService } from "../../services/nutrition/recipe/recipe.service";
import RecipeCard from "../../components/nutrition/RecipeCard";
import RecipeForm from "../../components/nutrition/RecipeForm";
import { SearchList } from "../../components/common/SearchList";
import { ingredientService } from "../../services/nutrition/ingredient/ingredient.service";

export default function Recipes() {
	const [recipes, setRecipes] = useState<RecipeDetailsDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [search, setSearch] = useState<string>("");
	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createRecipeOpen, setCreateRecipeOpen] = useState<boolean>(false);

	const [ingredients, setIngredients] = useState<RecipeIngredientResponseDTO[]>([]);
	const [selectedIngredientOpen, setSelectedIngredientOpen] = useState<boolean>(false);

	const fetchRecipes = async () => {
		try {
			setError(null);
			const data: PageResponseDTO<RecipeDetailsDTO> = await recipeService.getRecipes(page - 1, elementsPerPage, search);
			setRecipes(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch recipes");
		} finally {
			setLoading(false);
		}
	};

	const fetchAvailableRecipes = async () => {
		try {
			setError(null);
			const data: PageResponseDTO<RecipeDetailsDTO> = await recipeService.getAvailableRecipes(
				page - 1,
				elementsPerPage,
				ingredients.map(i => i.ingredient.id).join(",")
			);
			setRecipes(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch available recipes");
		} finally {
			setLoading(false);
		}
	};

	const createRecipe = async (recipe: RecipeDTO) => {
		try {
			setLoading(true);
			const data: RecipeDetailsDTO = await recipeService.createRecipe(recipe);
			setRecipes(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			toast.error("Failed to create recipe");
		} finally {
			setLoading(false);
		}
	};

	const updateRecipe = async (id: number, updatedData: RecipeDTO) => {
		try {
			const updated = await recipeService.updateRecipe(id, updatedData);
			setRecipes(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			toast.error("Failed to update recipe");
		}
	};

	const deleteRecipe = async (id: number) => {
		try {
			setLoading(true);
			await recipeService.deleteRecipe(id);
			fetchRecipes();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Failed to delete recipe");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		if (selectedIngredientOpen === false) {
			if (ingredients.length > 0) {
				console.log(ingredients.length);
				fetchAvailableRecipes();
			} else {
				fetchRecipes();
			}
		}
	}, [page, elementsPerPage, search, selectedIngredientOpen, ingredients.length]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load exercises"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchRecipes()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-center justify-between gap-4">
					<div className="flex flex-row gap-2 items-center">
						<button
							className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
							onClick={() => setCreateRecipeOpen(true)}
						>
							Create +
						</button>

						<IoMdOptions
							className="bg-primary hover:bg-primary/80 cursor-pointer p-2 rounded-lg"
							size={38}
							onClick={() => setSelectedIngredientOpen(true)}
						/>
						{ingredients.length > 0 &&
							<button className="rounded-3xl text-sm font-extralight cursor-pointer hidden sm:block" onClick={() => setIngredients([])}>Clear filters</button>
						}
					</div>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Recipes per page
							</label>

							<select
								id="exercisesPerPage"
								name="exercisesPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "recipe" : "recipes"} </span>
					</div>
				</div>

				{ingredients.length > 0 &&
					<button className="rounded-lg text-sm font-extralight cursor-pointer block sm:hidden bg-secondary/10 hover:bg-secondary/50 w-full p-2" onClick={() => setIngredients([])}>Clear filters</button>
				}

				{ingredients.length > 0 && (
					<div className="flex flex-wrap gap-2 bg-background/40 p-2 rounded-lg">
						{Array.from(ingredients.values()).map(item => (
							<span
								key={item.ingredient.id}
								onClick={() => {

									setIngredients(prev =>
										prev.filter(i => i.ingredient.id !== item.ingredient.id)
									);
									console.log(ingredients);
								}
								}
								className="
								px-3 py-1 rounded-full text-sm
								bg-primary/30 text-textcolor
								cursor-pointer hover:bg-primary/50
								select-none
							"
							>
								{item.ingredient.name}
								<span className="ml-1">×</span>
							</span>
						))}
					</div>
				)}

				{ingredients.length === 0 &&
					<SearchBar
						value={search}
						onChange={setSearch}
						placeholder="Recipe name..."
					/>
				}

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{recipes.map((recipe) => (
							<RecipeCard
								key={recipe.id}
								{...recipe}
								onUpdate={updateRecipe}
								onDelete={deleteRecipe}
							/>
						))}
					</div>
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createRecipeOpen} onClose={() => setCreateRecipeOpen(false)}>
				<RecipeForm
					onClose={() => setCreateRecipeOpen(false)}
					onCreate={createRecipe}
				/>
			</Modal>

			<Modal isOpen={selectedIngredientOpen} onClose={() => setSelectedIngredientOpen(false)}>
				<SearchList
					selectionMode="multiple"
					label="Available Ingredients"
					description="Select the ingredients you currently have to find recipes you can make."
					fetchItems={ingredientService.getIngredients}
					selectedIds={ingredients.map(i => i.ingredient.id)}
					selectedItems={ingredients.map(i => i.ingredient)}
					onChange={(items) => {
						setIngredients(prev => {
							const prevMap = new Map(prev.map(i => [i.ingredient.id, i]));
							return items.map(item =>
								prevMap.get(item.id) ?? {
									ingredient: item,
									amount: 0,
									unit: Unit.G,
								}
							);
						});
					}}
					onClose={() => setSelectedIngredientOpen(false)}
				/>
			</Modal>
		</>
	);
}
